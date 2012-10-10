package org.ucmtwine.parameter;

import intradoc.common.ExecutionContext;
import intradoc.common.SystemUtils;
import intradoc.data.DataBinder;
import intradoc.data.Workspace;
import intradoc.server.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.ucmtwine.annotation.Binder;
import org.ucmtwine.annotation.Environment;
import org.ucmtwine.parameter.types.InjectedParameter;

/**
 * Handles the binder parameters and type coercion for service and idoc methods
 * including parameter injection of context and user data when needed.
 * 
 * @author tim
 */
public class ParameterMarshaller {

  /**
   * The function def table stores each param type plus the function index, the
   * param count and return type
   */
  public static final int EXTRA_FUNC_DEF_VALUES = 3;

  private List<IParameter> parameters;

  public ParameterMarshaller() {
    this.parameters = new ArrayList<IParameter>();
  }

  public ParameterMarshaller(Method method) throws IllegalArgumentException {
    this.parameters = createList(method);
  }

  public List<IParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<IParameter> parameters) {
    this.parameters = parameters;
  }

  /**
   * Creates the list of BinderVariables given a method obtained through
   * reflection.
   * 
   * @param method
   *          The method
   * @return The list of BinderVariables
   */
  private static List<IParameter> createList(Method method) {

    List<IParameter> parameters = new ArrayList<IParameter>();

    Class<?> paramTypes[] = method.getParameterTypes();

    Annotation[][] methodAnnotations = method.getParameterAnnotations();

    for (int i = 0; i < methodAnnotations.length; i++) {

      // defaults
      boolean paramRequired = true;
      String paramName = "";
      Class<?> paramClass = paramTypes[i];
      
      for (Annotation a : methodAnnotations[i]) {
        if (a instanceof Binder) {
          paramName = ((Binder) a).name();
          paramRequired = ((Binder) a).required();
          break;
        }
//        else if (a instanceof Environment) {
//          paramName = ((Environment) a).name();
//          paramRequired = ((Environment) a).required();
//          break;
//        }
      }

      parameters.add(Parameter.create(paramName, paramClass, paramRequired));
    }

    return parameters;
  }

  /**
   * Returns an array of types for use with reflection method.invoke().
   * 
   * @return
   */
  public Class<?>[] getTypeArray() {

    Class<?>[] typeArray = new Class[this.parameters.size()];

    for (int i = 0; i < this.parameters.size(); i++) {
      typeArray[i] = this.parameters.get(i).getType();
    }

    return typeArray;
  }

  /**
   * 
   * @param includeInjected
   * @return
   */
  public int getParameterCount(boolean includeInjected) {
    int count = 0;

    for (int i = 0; i < this.parameters.size(); i++) {
      if (this.parameters.get(i) instanceof InjectedParameter) {
        if (includeInjected) {
          count++;
        }

      } else {
        count++;
      }
    }

    return count;
  }

  /**
   * Return the m_functionDefinition compatible array.
   * 
   * @param maxParams
   * @return
   */
  public int[] getFunctionDefinitionArray(int functionOffset, int maxParams, int returnType) {
    int[] definition = new int[maxParams + EXTRA_FUNC_DEF_VALUES];

    int paramCount = getParameterCount(false);
    int fullParamCount = getParameterCount(true);

    definition[0] = functionOffset;

    if (paramCount > maxParams) {
      throw new IllegalStateException("Attempted to get function definition table when supplied "
          + "max parameter count " + maxParams + " is smaller than real param count " + paramCount);
    }

    definition[1] = paramCount;

    int j = 2;

    for (int i = 0; i < fullParamCount; i++) {
      // add grammar element if it is NOT an injected type
      if ((this.parameters.get(i) instanceof InjectedParameter) == false) {
        definition[j] = this.parameters.get(i).getGrammarElementType();
        j++;
      }
    }

    // pad out unspecified param types
    for (; j < definition.length - 1; j++) {
      definition[j] = Parameter.GRAMMAR_ELEMENT_UNSPECIFIED;
    }

    // return type as last value
    definition[definition.length - 1] = returnType;

    return definition;
  }

  /**
   * Returns an array of values given an array of script arguments for use with
   * the reflection method.invoke(), injecting known service/user/context data.
   * 
   * @return
   * @throws IllegalAccessException
   */
  public Object[] getValueArray(Object[] arguments, Service service) {
    Object[] paramArray = new Object[this.parameters.size()];

    int argumentIndex = 0;

    for (int i = 0; i < this.parameters.size(); i++) {

      // inject params if needed
      IParameter p = this.parameters.get(i);

      try {

        if (p instanceof InjectedParameter) {
          paramArray[i] = p.getArgumentValue(null, service);
        } else {
          paramArray[i] = p.getArgumentValue(arguments[argumentIndex], service);
          argumentIndex++;
        }

      } catch (ClassCastException e) {
        SystemUtils.trace("twine", "getArgumentValue failed on parameter " + (i + 1) + ": " + e.getMessage());
      }

    }

    return paramArray;
  }

  /**
   * Returns an array of values given a filter execution context
   * 
   * @param ws
   *          The system workspace
   * @param binder
   *          The current binder
   * @param ctx
   *          The current execution context
   * @return
   */
  public Object[] getValueArray(Workspace ws, DataBinder binder, ExecutionContext ctx) {
    Object[] paramArray = new Object[this.parameters.size()];

    for (int i = 0; i < this.parameters.size(); i++) {

      // inject params if needed
      IParameter p = this.parameters.get(i);

      try {

        if (p.getType() == Workspace.class) {
          paramArray[i] = ws;

        } else if (p.getType() == DataBinder.class) {
          paramArray[i] = binder;

        } else if (p.getType().isAssignableFrom(ExecutionContext.class)) {
          paramArray[i] = ctx;

        } else {
          paramArray[i] = null;
        }

      } catch (ClassCastException e) {
        SystemUtils.trace("twine", "getArgumentValue failed on parameter " + (i + 1) + ": " + e.getMessage());
      }

    }

    return paramArray;
  }

  /**
   * Returns an array of values for use with the reflection method.invoke(),
   * injecting known service/user/context data.
   * 
   * @return
   * @throws IllegalAccessException
   */
  public Object[] getValueArray(Service service) {
    Object[] paramArray = new Object[this.parameters.size()];

    for (int i = 0; i < this.parameters.size(); i++) {

      IParameter p = this.parameters.get(i);

      try {
        paramArray[i] = p.getBinderValue(service);

      } catch (IllegalAccessException e) {
        SystemUtils.trace("twine", "getValue failed on parameter " + (i + 1) + ": " + e.getMessage());
      }

    }

    return paramArray;
  }
}
