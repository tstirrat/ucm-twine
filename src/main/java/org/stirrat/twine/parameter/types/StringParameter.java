package org.stirrat.twine.parameter.types;

import intradoc.common.GrammarElement;
import intradoc.server.Service;

import org.stirrat.twine.parameter.Parameter;

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
