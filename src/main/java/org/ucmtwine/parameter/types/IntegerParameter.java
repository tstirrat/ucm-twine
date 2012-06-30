package org.ucmtwine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.server.Service;

import org.ucmtwine.parameter.Parameter;

public class IntegerParameter extends Parameter {

  public IntegerParameter(Class<?> type) {
    super(type);
  }

  public IntegerParameter() {
    super(Integer.class);
  }

  public IntegerParameter(String name) {
    super(name, Boolean.class);
  }

  public IntegerParameter(String name, Class<?> type) {
    super(name, type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getBinderValue(Service service) {
    String integerString = getStringValue(service.getBinder());

    return parseStringValue(integerString);
  }

  private Object parseStringValue(String integerString) {
    Integer value = null;

    try {
      value = Integer.parseInt(integerString);

    } catch (NumberFormatException e) {

      // null is ok, but invalid int is not.
      if (integerString != null) {
        throw new IllegalArgumentException("Parameter " + name + " must be parseable as an integer");
      }
    }

    return value;
  }

  @Override
  public Object getArgumentValue(Object object, Service service) throws ClassCastException {
    if (object == null) {
      return null;
    }

    if (object.getClass() == String.class) {
      return parseStringValue((String) object);
    }

    return super.getArgumentValue(object, service);
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    return GrammarElement.INTEGER_VAL;
  }
}
