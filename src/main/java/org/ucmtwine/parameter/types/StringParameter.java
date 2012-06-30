package org.ucmtwine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.server.Service;

import org.ucmtwine.parameter.Parameter;

public class StringParameter extends Parameter {

  public StringParameter(String name, Class<?> type) {
    super(name, type);
  }

  @Override
  public Object getBinderValue(Service service) {
    return getStringValue(service.getBinder());
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    return GrammarElement.STRING_VAL;
  }
}
