package org.ucmtwine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.server.Service;

import org.ucmtwine.parameter.Parameter;

public class BooleanParameter extends Parameter {

  public BooleanParameter() {
    super(Boolean.class);
  }

  public BooleanParameter(Class<?> type) {
    super(type);
  }

  public BooleanParameter(String name, Class<?> type) {
    super(name, type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getBinderValue(Service service) {
    String binderValue = getStringValue(service.getBinder());

    return parseStringValue(binderValue);
  }

  private Boolean parseStringValue(String binderValue) {
    Boolean val = null;

    if (binderValue != null) {

      if (binderValue.equals("1")) {
        val = new Boolean(true);
      } else {
        val = new Boolean(binderValue);
      }
    }

    return val;
  }

  /**
   * {@inheritDoc}
   */
  public Object getArgumentValue(Object object, Service service) throws ClassCastException {
    if (object == null) {
      return null;
    }

    if (object.getClass() == Long.class) {
      return (Long) object == 1L;
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
