package org.stirrat.twine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.data.DataResultSet;
import intradoc.data.ResultSet;
import intradoc.server.Service;

import org.stirrat.twine.parameter.Parameter;

public class ResultSetParameter extends Parameter {

  public ResultSetParameter() {
    super("", DataResultSet.class);
  }

  public ResultSetParameter(String name) {
    super(name, DataResultSet.class);
  }

  public ResultSetParameter(String name, Class<?> type) {
    super(name, type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getBinderValue(Service service) {
    DataResultSet drs = getResultSet(this.name, service);

    if (this.required && drs == null) {
      throw new IllegalArgumentException("ResultSet " + this.name + " is required.");
    }

    return drs;
  }

  /**
   * Returns a result set found by the passed in string value.
   */
  @Override
  public Object getArgumentValue(Object object, Service service) throws ClassCastException {

    if (object instanceof String) {
      DataResultSet drs = getResultSet((String) object, service);

      if (drs != null) {
        return drs;
      }
    }
    return null;
    // throw new ClassCastException("Must supply the name of a result set as a string");
  }

  /**
   * Find a result set in the service binder.
   * 
   * @param name
   * @param service
   * @return
   */
  private DataResultSet getResultSet(String name, Service service) {
    ResultSet rs = service.getBinder().getResultSet(name);
    DataResultSet drs = new DataResultSet();

    if (rs != null) {
      drs.copy(rs);

      return drs;
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    // string val because the RS name is passed as a string
    return GrammarElement.STRING_VAL;
  }
}
