package org.stirrat.twine.parameter;

import intradoc.data.DataBinder;
import intradoc.data.DataResultSet;
import intradoc.data.ResultSet;
import intradoc.server.Service;

import java.util.Date;

import net.balusc.util.ObjectConverter;

import org.apache.commons.lang.ClassUtils;
import org.stirrat.twine.parameter.types.BooleanParameter;
import org.stirrat.twine.parameter.types.DateParameter;
import org.stirrat.twine.parameter.types.DoubleParameter;
import org.stirrat.twine.parameter.types.FloatParameter;
import org.stirrat.twine.parameter.types.InjectedParameter;
import org.stirrat.twine.parameter.types.IntegerParameter;
import org.stirrat.twine.parameter.types.LongParameter;
import org.stirrat.twine.parameter.types.ResultSetParameter;
import org.stirrat.twine.parameter.types.StringParameter;

/**
 * Encapsulates a binder parameter, taking the legwork out of enforcing type and
 * mandatory requirements.
 * 
 * @author tim
 */
public abstract class Parameter implements IParameter {

  public static final int GRAMMAR_ELEMENT_UNSPECIFIED = -1;

  protected String name;
  protected boolean required = true;

  /**
   * The actual type (may be primitive, non nullable etc)
   */
  protected Class<?> type;

  protected Parameter() {
    this.name = "";
  }

  protected Parameter(Class<?> type) {
    this.type = type;
    this.name = "";
  }

  /**
   * Initialise a basic named binder variable.
   * 
   * @param name
   */
  protected Parameter(String name, Class<?> type) {
    this.name = name;
    this.type = type;
  }

  /**
   * Factory method to create the concrete variable type.
   * 
   * @param name
   *          The name in the binder of the variable.
   * @return
   */
  public static Parameter create(String name) {
    return create(name, String.class, true);
  }

  public static Parameter create(String typeString, String name, String requiredString) throws ClassNotFoundException,
      IllegalArgumentException {

    Class<?> type = getTypeFromString(typeString);

    return create(name, type, parseRequiredString(requiredString));
  }

  public static Class<?> getTypeFromString(String typeString) throws ClassNotFoundException {
    return ClassUtils.getClass(typeString);
  }

  /**
   * Create a standard, required, parameter.
   * 
   * @param type
   * @return
   */
  public static Parameter create(Class<?> type) {
    return create("", type, true);
  }

  public static Parameter create(String name, Class<?> type, boolean required) throws IllegalArgumentException {

    if (InjectedParameter.isValidType(type)) {
      return new InjectedParameter(name, type);
    }

    if (type.isPrimitive() && !required) {
      String msg = "Parameter [" + name + "] found with non-nullable type. Use a wrapper type or change to required";
      throw new IllegalArgumentException(msg);
    }

    Parameter p;

    if (type == String.class) {
      p = new StringParameter(name, type);

    } else if (type == Integer.class || type == int.class) {
      p = new IntegerParameter(name, type);

    } else if (type == Long.class || type == long.class) {
      p = new LongParameter(name, type);

    } else if (type == Float.class || type == float.class) {
      p = new FloatParameter(name, type);

    } else if (type == Double.class || type == double.class) {
      p = new DoubleParameter(name, type);

    } else if (type == Date.class) {
      p = new DateParameter(name, type);

    } else if (type == ResultSet.class || type == DataResultSet.class) {
      p = new ResultSetParameter(name, type);

    } else if (type == Boolean.class || type == boolean.class) {
      p = new BooleanParameter(name, type);

    } else {
      throw new IllegalArgumentException("Parameter type " + type.getName() + " is not valid");
    }

    p.setRequired(required);

    return p;
  }

  /**
   * Determines the classname to assign given a string "type" representation.
   * 
   * @param typeString
   * @return
   */
  public static Class<?> parseTypeString(String typeString) {

    if (typeString.equalsIgnoreCase("string")) {
      return String.class;

    } else if (typeString.equalsIgnoreCase("int") || typeString.equalsIgnoreCase("Integer")) {
      return Integer.class;

    } else if (typeString.equalsIgnoreCase("Float") || typeString.equalsIgnoreCase("Double")) {
      return Float.class;

    } else if (typeString.equalsIgnoreCase("Date")) {
      return Date.class;

    } else if (typeString.equalsIgnoreCase("resultset") || typeString.equalsIgnoreCase("DataResultSet")) {
      return DataResultSet.class;

    } else if (typeString.equalsIgnoreCase("bool") || typeString.equalsIgnoreCase("boolean")) {
      return Boolean.class;

    } else {
      throw new IllegalArgumentException("Parameter type " + typeString + " is not known");
    }
  }

  /**
   * Get variable name.
   * 
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Set variable name.
   * 
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean optional) {
    this.required = optional;
  }

  /**
   * Get the string value from binder.
   */
  public String getStringValue(DataBinder binder) {
    // binder can't get a non existent param
    if (this.name == null || this.name.equals("")) {
      return null;
    }

    String value = (String) binder.getLocal(this.name);

    if (value == null && this.required) {
      throw new IllegalArgumentException("Parameter " + name + " is required");
    }

    return value;
  }

  /**
   * {@inheritDoc}
   */
  abstract public Object getBinderValue(Service service) throws IllegalAccessException;

  /**
   * {@inheritDoc}
   */
  public Object getArgumentValue(Object object, Service service) throws ClassCastException {
    return ObjectConverter.convert(object, type);
  }

  /**
   * {@inheritDoc}
   */
  public String toActionString() {
    return this.getType().getName() + "," + this.getName() + "," + String.valueOf(this.isRequired());
  }

  /**
   * Based on an input string, determines if the required flag is true.
   * 
   * @param requiredString
   *          A string containing a boolean value or "required".
   * 
   * @return A boolean representing the mandatory state of the binder variable
   */
  public static boolean parseRequiredString(String requiredString) {
    return Boolean.parseBoolean(requiredString) || requiredString.equalsIgnoreCase("required");
  }

  /**
   * Returns the actual type given a type string.
   * 
   * @return A java type, if valid
   */
  public Class<?> getType() {
    return this.type;
  }
}