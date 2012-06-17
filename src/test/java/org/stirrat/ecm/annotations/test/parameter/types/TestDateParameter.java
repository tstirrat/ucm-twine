package org.stirrat.ecm.annotations.test.parameter.types;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.server.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.stirrat.twine.parameter.types.DateParameter;

@RunWith(MockitoJUnitRunner.class)
public class TestDateParameter {

  private DataBinder testBinder;

  @Mock
  private Service testService;

  Date nowWithoutMillis;

  Date now;

  @Before
  public void setUp() throws DataException, ServiceException {

    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());

    now = cal.getTime();

    cal.set(Calendar.MILLISECOND, 0);

    nowWithoutMillis = cal.getTime();

    Long timestamp = cal.getTimeInMillis();

    testBinder = new DataBinder();

    SimpleDateFormat sdf = new SimpleDateFormat(testBinder.m_blDateFormat.toSimplePattern());
    String nowString = sdf.format(nowWithoutMillis);

    testBinder.putLocal("empty", "");
    testBinder.putLocal("timestamp", timestamp.toString());
    testBinder.putLocal("one", "1");
    testBinder.putLocal("enAU", nowString);
    testBinder.putLocal("negativeParam", "-1");
    testBinder.putLocal("positiveParam", "200");

    when(testService.getBinder()).thenReturn(testBinder);
  }

  @Test
  public void testParsesTimestampFromString() {
    Date d = (Date) new DateParameter("enAU").getBinderValue(testService);
    assertEquals(0, d.compareTo(nowWithoutMillis));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyBinderParameterThrowsError() {
    new DateParameter("empty").getBinderValue(testService);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonExistentBinderParameterThrowsError() {
    new DateParameter("ralph").getBinderValue(testService);
  }

  @Test
  public void testArgumentDateParameter() {
    Date d = (Date) new DateParameter("empty").getArgumentValue(now, testService);
    assertEquals(0, d.compareTo(now));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBinderIntegerParameterFails() {
    new DateParameter("timestamp").getBinderValue(testService);
  }
}
