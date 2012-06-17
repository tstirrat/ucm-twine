package org.stirrat.twine.parameter;

import intradoc.server.Service;

public interface IParameter {

  public String getName();

  public boolean isRequired();

  /**
   * Get the value from the binder, checking for type compatibility and
   * mandatory requirements.
   * 
   * @param service
   *          The execution context.
   * @return A type cast value or null
   * @throws IllegalAccessException
   * 
   */
  public Object getBinderValue(Service service) throws IllegalAccessException;

  /**
   * Get the value from another object, casting if needed.
   * 
   * @param object
   * @return
   * @throws ClassCastException
   */
  public Object getArgumentValue(Object object, Service service) throws ClassCastException;

  /**
   * Returns the class of the binder variable after type coercion.
   * 
   * @return
   */
  public Class<?> getType();

  /**
   * Gets a parameter definition string for use in service actions.
   * 
   * @return
   */
  public String toActionString();

  /**
   * Returns the GrammarElement type for use in idoc script function
   * definitions.
   * 
   * @return
   */
  public int getGrammarElementType();
}
