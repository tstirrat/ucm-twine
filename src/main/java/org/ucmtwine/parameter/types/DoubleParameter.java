package org.ucmtwine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.server.Service;

import org.ucmtwine.parameter.Parameter;

public class DoubleParameter extends Parameter {

  public DoubleParameter() {
    super("", Double.class);
  }

  public DoubleParameter(String name) {
    super(name, Double.class);
  }

  public DoubleParameter(String name, Class<?> type) {
    super(name, type);
  }

  @Override
  public Object getBinderValue(Service service) {

    String doubleString = getStringValue(service.getBinder());

    Double value = null;

    try {
      value = Double.parseDouble(doubleString);

    } catch (NumberFormatException e) {

      // null is ok (unless param is required) but invalid parse is not.
      if (doubleString != null) {
        throw new IllegalArgumentException("Parameter " + name + " must be parseable as a double");
      }
    }

    return value;
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    return GrammarElement.FLOAT_VAL;
  }
}
