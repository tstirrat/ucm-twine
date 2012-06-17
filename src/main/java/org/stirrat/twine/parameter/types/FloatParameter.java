package org.stirrat.twine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.server.Service;

import org.stirrat.twine.parameter.Parameter;

public class FloatParameter extends Parameter {

  public FloatParameter() {
    super("", Float.class);
  }

  public FloatParameter(String name) {
    super(name, Float.class);
  }

  public FloatParameter(String name, Class<?> type) {
    super(name, type);
  }

  @Override
  public Object getBinderValue(Service service) {
    
    String floatString = getStringValue(service.getBinder());

    Float value = null;

    try {
      value = Float.parseFloat(floatString);

    } catch (NumberFormatException e) {

      // null is ok (unless param is required) but invalid parse is not.
      if (floatString != null) {
        throw new IllegalArgumentException("Parameter " + name + " must be parseable as a float");
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
