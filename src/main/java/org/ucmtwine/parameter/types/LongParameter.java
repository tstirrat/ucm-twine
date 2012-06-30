package org.ucmtwine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.server.Service;

import org.ucmtwine.parameter.Parameter;

public class LongParameter extends Parameter {

  public LongParameter() {
    super("", Long.class);
  }

  public LongParameter(String name) {
    super(name, Long.class);
  }

  public LongParameter(String name, Class<?> type) {
    super(name, type);
  }

  @Override
  public Object getBinderValue(Service service) {
    String longString = getStringValue(service.getBinder());

    Long value = null;

    try {
      value = Long.parseLong(longString);

    } catch (NumberFormatException e) {

      // null is ok, but invalid int is not.
      if (longString != null) {
        throw new IllegalArgumentException("Parameter " + name + " must be parseable as type Long");
      }
    }

    return value;
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    return GrammarElement.INTEGER_VAL;
  }
}
