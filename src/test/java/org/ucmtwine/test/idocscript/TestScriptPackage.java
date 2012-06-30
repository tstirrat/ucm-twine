package org.ucmtwine.test.idocscript;

import intradoc.common.ExecutionContext;
import intradoc.common.LocaleUtils;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.server.PageMerger;
import intradoc.server.Service;
import intradoc.shared.UserData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.ucmtwine.annotation.IdocFunction;
import org.ucmtwine.annotation.IdocVariable;

/**
 * Example script package replicating the HowToComponents script extensions.
 * 
 * @author tim
 */
public class TestScriptPackage {

  @IdocVariable(name = "UppercaseUserName")
  public String uppercaseUserName(ExecutionContext context) throws ServiceException {
    UserData userData = (UserData) context.getCachedObject("UserData");
    if (userData == null) {
      String msg = LocaleUtils.encodeMessage("csUserDataNotAvailable", null, "UppercaseUserName");
      throw new ServiceException(msg);
    }

    // set it to upper case
    return userData.getProperty("dFullName").toUpperCase();
  }

  @IdocVariable(name = "TodaysDateIsEven")
  public boolean todaysDateIsEven() {
    // prepare a simple date format object to display the day of month
    SimpleDateFormat dateFrmt = new SimpleDateFormat("d");

    // set the timezone
    dateFrmt.setTimeZone(TimeZone.getDefault());

    // use the format object to display the day of month for this date
    String dateNumString = dateFrmt.format(new Date());

    // convert it into an integer
    int dateNum = Integer.valueOf(dateNumString).intValue();

    // if its even, change bResult from the default 'false' to 'true'
    if ((dateNum % 2) == 0) {
      return true;
    }

    return false;
  }

  @IdocFunction
  public long factorial(long input) {
    // If the integer is negative, we wish to return an error
    // string, and not an integer.
    if (input < 0) {
      // print the error message to the display
      throw new IllegalArgumentException("Cannot perform a factorial function on the negative number '" + input + "'");
    }

    // otherwise, perform a simple factorial algorithm on iResult
    long result = 1L;
    for (int i = 1; i <= input; i++) {
      result = result * i;
    }

    return result;
  }

  /**
   * Log a string to the console output so a developer can debug IdocScript. The
   * console can be viewed from the 'View Server Output' link on the Admin
   * Server. It can also be viewed if the server is started from the console as
   * opposed to being run as an NT Service. This function is handy for debugging
   * IdocScript pages.
   * 
   * @param message
   *          The message to log
   * @param binder
   *          The current data binder
   * @param service
   *          The current service
   */
  @IdocFunction
  public void log(String message, DataBinder binder, Service service) {

    try {
      // if the key they pass is '#LocalData', dump everything!
      if (message.equalsIgnoreCase("#LocalData")) {
        String name, value;

        // obtain a list of all the name value-pairs in the local
        // data
        @SuppressWarnings("rawtypes")
        java.util.Enumeration e = binder.getLocalData().keys();

        System.out.println("\nLocalData:");

        // loop over the list of names, and print out the name-value
        // pairs on a new line.
        while (e.hasMoreElements()) {
          name = (String) e.nextElement();
          value = binder.getLocal(name);
          if (value != null && value.length() > 0) {
            System.out.println("  " + name + "=" + value);
          }
        }
      } else {
        // obtain a PageMerger object from the service object
        // obtained above. This PageMerger object can be used
        // anywhere to evaluate IdocScript based on the data
        // in a DataBinder. This PageMerger object was initialized
        // with the data in the service call, and also contains
        // all the values set in IdocScript on the template page
        // for the response.
        PageMerger pm = service.getPageMerger();

        // the first string argument (message) is a String containing
        // IdocScript which can be evaluated. The PageMerger object
        // evaluates it, and returns some plain text, or even a
        // chunk of html
        String str = pm.evaluateScript(message);

        // print this value to the standard output.
        System.out.println(str);
      }
    } catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Looks at the 2 strings passed, and returns the one that occurs first in
   * alphabetical order.
   * 
   * @param a
   *          The first string to compare
   * @param b
   *          The second string to compare
   * @return The first occurring string
   */
  @IdocFunction
  public String strMin(String a, String b) {
    int result = a.toLowerCase().compareTo(b.toLowerCase());

    // set the result value to the one that occurs first, or the first
    // entry if they are lexigraphically equivalent
    if (result <= 0) {
      return a;
    }

    return b;
  }
}
