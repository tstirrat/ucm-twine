package org.ucmtwine.proxy;

import intradoc.common.ExecutionContext;
import intradoc.common.LocaleUtils;
import intradoc.common.ScriptExtensionsAdaptor;
import intradoc.common.ScriptInfo;
import intradoc.common.ServiceException;
import intradoc.common.SystemUtils;
import intradoc.server.Service;
import intradoc.server.script.ScriptExtensionUtils;
import intradoc.shared.UserData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.balusc.util.ObjectConverter;

import org.ucmtwine.annotation.IdocFunction;
import org.ucmtwine.annotation.IdocVariable;
import org.ucmtwine.parameter.ParameterMarshaller;

public class ScriptProxy extends ScriptExtensionsAdaptor {
  private List<Class<?>[]> functionParameterTypes;

  private List<Class<?>[]> variableParameterTypes;

  private List<String> functionMethodNames;

  private List<String> variableMethodNames;

  private Class<?> m_class;

  private Method functionMethods[];

  public static final int RETURN_VOID = -1;
  public static final int RETURN_STRING = 0;
  public static final int RETURN_BOOLEAN = 1;
  public static final int RETURN_INTEGER = 2;
  public static final int RETURN_FLOAT = 3;

  public static final int NONE = -1;

  public ScriptProxy(Class<?> klass) {
    m_class = klass;
    generateDefinitions();
  }

  /**
   * Configuration data for functions. This list must align with the
   * "m_functionTable" list. In order the values are "id number",
   * "Number of arguments", "First argument type", "Second argument type",
   * "Return Type". Return type has the following possible values: 0 generic
   * object (such as strings) 1 boolean 2 integer 3 double. The value "-1" means
   * the value is unspecified.
   * 
   * @param klass
   *          The annotated class
   */
  private void generateDefinitions() {
    functionParameterTypes = new ArrayList<Class<?>[]>();
    functionMethodNames = new ArrayList<String>();
    variableParameterTypes = new ArrayList<Class<?>[]>();
    variableMethodNames = new ArrayList<String>();

    List<String> functionNames = new ArrayList<String>();
    List<ParameterMarshaller> functionParams = new ArrayList<ParameterMarshaller>();
    List<Integer> functionReturnTypes = new ArrayList<Integer>();

    List<String> variableNames = new ArrayList<String>();
    List<Integer> variableReturnTypes = new ArrayList<Integer>();

    Method ms[] = m_class.getDeclaredMethods();

    functionMethods = new Method[ms.length];

    Map<String, Method> methods = new TreeMap<String, Method>();

    for (Method m : ms) {
      methods.put(m.getName(), m);
    }

    int functionCounter = 0;

    for (String methodName : methods.keySet()) {
      Method m = methods.get(methodName);
      IdocFunction functionInfo = m.getAnnotation(IdocFunction.class);

      if (functionInfo != null) {
        if (functionInfo.name().equals("")) {
          functionNames.add(m.getName());
        } else {
          functionNames.add(functionInfo.name());
        }
        functionMethodNames.add(m.getName());
        functionMethods[functionCounter] = m;
        functionParams.add(new ParameterMarshaller(m));
        functionReturnTypes.add(getFunctionReturnType(m));
        functionParameterTypes.add(m.getParameterTypes());
        functionCounter++;

      } else {
        IdocVariable varInfo = m.getAnnotation(IdocVariable.class);

        if (varInfo != null) {
          if (varInfo.name().equals("")) {
            variableNames.add(m.getName());
          } else {
            variableNames.add(varInfo.name());
          }
          variableMethodNames.add(m.getName());
          variableReturnTypes.add(getVariableReturnType(m));
          variableParameterTypes.add(m.getParameterTypes());
        }
      }
    }

    writeVariableTables(variableNames, variableReturnTypes);
    writeFunctionTables(functionNames, functionParams, functionReturnTypes);
  }

  /**
   * Build a static int[m][2] array of variable info where the int array is
   * <code>{ variable_index, return_type }</code>.
   * 
   * @param functionParams
   */
  private void writeVariableTables(List<String> variableNames, List<Integer> variableReturnTypes) {
    m_variableTable = variableNames.toArray(new String[variableNames.size()]);

    m_variableDefinitionTable = new int[variableNames.size()][2];

    for (int i = 0; i < variableNames.size(); i++) {
      m_variableDefinitionTable[i][0] = i;
      m_variableDefinitionTable[i][1] = variableReturnTypes.get(i);
    }
  }

  /**
   * Build a static int[m][n] array of function info from a dynamic array where
   * n changes size. The last parameter will always remain the last parameter
   * but any short rows will pad their parameter types as -1 (type not
   * specified)
   * 
   * @param functionParams
   */
  private void writeFunctionTables(List<String> functionNames, List<ParameterMarshaller> functionParams,
      List<Integer> returnTypes) {
    m_functionTable = functionNames.toArray(new String[functionNames.size()]);

    int maxParams = 0;

    // find the max of all parameter counts
    for (int i = 0; i < functionParams.size(); i++) {
      int paramCount = functionParams.get(i).getParameterCount(false);
      if (paramCount > maxParams) {
        maxParams = paramCount;
      }
    }

    m_functionDefinitionTable = new int[functionParams.size()][maxParams + ParameterMarshaller.EXTRA_FUNC_DEF_VALUES];

    // output params now
    for (int i = 0; i < functionParams.size(); i++) {
      m_functionDefinitionTable[i] = functionParams.get(i).getFunctionDefinitionArray(i, maxParams, returnTypes.get(i));
    }
  }

  /**
   * Get idoc function return type.
   * 
   * @param m
   * @return
   */
  public Integer getFunctionReturnType(Method m) {
    Class<?> type = m.getReturnType();

    if (type == Void.class || type == void.class) {
      return RETURN_VOID;
    }

    if (type == Boolean.class || type == boolean.class) {
      return RETURN_BOOLEAN;
    }

    if (type == Integer.class || type == int.class || type == Long.class || type == long.class) {
      return RETURN_INTEGER;
    }

    if (type == Float.class || type == float.class || type == Double.class || type == double.class) {
      return RETURN_FLOAT;
    }

    return RETURN_STRING;
  }

  /**
   * Get idoc variable type (limited to string or conditional).
   * 
   * @param m
   * @return
   */
  public Integer getVariableReturnType(Method m) {
    if (m.getReturnType() == Boolean.class || m.getReturnType() == boolean.class) {
      return RETURN_BOOLEAN;
    }
    return RETURN_STRING;
  }

  /**
   * This is where the custom IdocScript function is evaluated.
   */
  public boolean evaluateFunction(ScriptInfo info, Object[] args, ExecutionContext context) throws ServiceException {
    /**
     * This code below is optimized for speed, not clarity. Do not modify the
     * code below when making new IdocScript functions. It is needed to prepare
     * the necessary variables for the evaluation and return of the custom
     * IdocScript functions. Only customize the switch statement below.
     */
    int config[] = (int[]) info.m_entry;
    String functionCalled = info.m_key;
    int functionIndex = config[0];

    int nargs = args.length - 1;
    int allowedParams = config[1];
    if (allowedParams >= 0 && allowedParams != nargs) {
      String msg = LocaleUtils.encodeMessage("csScriptEvalNotEnoughArgs", null, functionCalled, "" + allowedParams);
      throw new IllegalArgumentException(msg);
    }

    UserData userData = (UserData) context.getCachedObject("UserData");
    if (userData == null) {
      String msg = LocaleUtils.encodeMessage("csUserDataNotAvailable", null, functionCalled);
      throw new ServiceException(msg);
    }

    if (functionIndex > m_functionTable.length) {
      SystemUtils.trace("twine", "Unknown function with index" + functionIndex);
      return false;
    }

    try {
      args[nargs] = runFunctionMethod(functionIndex, args, context);
    } catch (Exception e) {
      String msg = e.getMessage();
      if (e instanceof InvocationTargetException) {
        msg = ((InvocationTargetException) e).getTargetException().getMessage();
      }
      msg = "Unable to execute function '" + functionCalled + "()': " + msg;
      SystemUtils.err(e, msg);
      SystemUtils.trace("twine", msg);

      throw new ServiceException(e);
    }

    // Handled function.
    return true;
  }

  /**
   * Enumerates the correct parameters for the delegated method. Injects any
   * autowired types if present in the method signature.
   * 
   * @param method
   * @param args
   * @param ctx
   * @return
   * @throws IllegalArgumentException
   * @throws ServiceException
   */
  public Object[] getInjectedValueArray(Method method, Object[] args, ExecutionContext ctx)
      throws IllegalArgumentException, ServiceException {

    ParameterMarshaller marshaller = new ParameterMarshaller(method);

    if ((ctx instanceof Service) == false) {
      throw new ServiceException("Tried to create parameters with injection and not inside a service.");
    }

    return marshaller.getValueArray(args, (Service) ctx);
  }

  /**
   * Executes the annotated method.
   * 
   * @param functionIndex
   * @param args
   * @param ctx
   * @return
   * @throws IllegalArgumentException
   * @throws ServiceException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public Object runFunctionMethod(int functionIndex, Object[] args, ExecutionContext ctx) throws SecurityException,
      NoSuchMethodException, IllegalArgumentException, ServiceException, IllegalAccessException,
      InvocationTargetException {
    Method method = functionMethods[functionIndex];

    Object params[] = getInjectedValueArray(method, args, ctx);

    Object result;
    try {
      result = method.invoke(m_class.newInstance(), params);

    } catch (InstantiationException e) {
      // TODO catch and re-throw ewwwww
      throw new ServiceException("Cannot delegate instantiate script context: " + e.getMessage());
    }

    if (result == null) {
      return result;
    }

    return convertReturnValue(result);
  }

  /**
   * Convert the method return value into a PageMerger internal type of String,
   * Long or Double
   * 
   * @param result
   * @return
   */
  private Object convertReturnValue(Object result) {
    if (boolean.class.isInstance(result) || result instanceof Boolean) {
      return ScriptExtensionUtils.computeReturnObject(1, ((Boolean) result).booleanValue(), 0, 0.0, null);

    } else if (long.class.isInstance(result)) {
      return (Long) result;

    } else if (int.class.isInstance(result) || result instanceof Integer) {
      return new Long((Integer) result);

    } else if (double.class.isInstance(result)) {
      return (Double) result;

    }

    // String/Double/Long/Float
    return result;
  }

  /**
   * This is where the custom IdocScript variable is evaluated.
   */
  public boolean evaluateValue(ScriptInfo info, boolean[] returnBool, String[] returnString, ExecutionContext context,
      boolean isConditional) throws ServiceException {
    /**
     * This code, like the beginning block of code in evaluateFunction, is
     * required for preparing the data for evaluation. It should not be altered.
     * Only customize the switch statement below.
     */
    int config[] = (int[]) info.m_entry;
    String key = info.m_key;

    if ((context instanceof Service) == false) {
      // Some variables will evaluate trivially instead of throwing an
      // exception.
      if (config[1] == RETURN_BOOLEAN) {
        returnBool[0] = false;
        returnString[0] = "";
        return true;
      }

      throw new ServiceException("Script variable " + key + " must have be evaluated in "
          + "context of a Service object.");
    }

    UserData userData = (UserData) context.getCachedObject("UserData");

    if (userData == null) {
      throw new ServiceException("Script variable " + key + " must have user data context.");
    }

    int variableIndex = config[0];
    String variableRequested = m_variableTable[variableIndex];

    if (variableIndex > m_variableTable.length) {
      return false; // unknown variable
    }

    Object result = null;

    try {
      result = runVariableMethod(variableIndex, context);

    } catch (Exception e) {
      String msg = "Unable to handle variable " + variableRequested + ": " + e.getMessage();
      SystemUtils.err(e, msg);
      SystemUtils.trace("twine", msg);
      throw new ServiceException(msg);
    }

    if (isConditional) {
      returnBool[0] = ObjectConverter.convert(result, boolean.class);
    } else {
      returnString[0] = ObjectConverter.convert(result, String.class);
    }

    return true;
  }

  public Object runVariableMethod(int variableIndex, ExecutionContext ctx) throws SecurityException,
      NoSuchMethodException, IllegalArgumentException, ServiceException, IllegalAccessException,
      InvocationTargetException {
    Method method = m_class
        .getMethod(variableMethodNames.get(variableIndex), variableParameterTypes.get(variableIndex));

    // faked argument array for injection.
    Object args[] = { 0 };

    Object params[] = getInjectedValueArray(method, args, ctx);

    Object result;
    try {
      result = method.invoke(m_class.newInstance(), params);

    } catch (InstantiationException e) {
      // TODO catch and re-throw ewwwww
      throw new ServiceException("Cannot delegate instantiate script context: " + e.getMessage());
    }

    if (result.getClass() == boolean.class || result.getClass() == Boolean.class) {
      return ScriptExtensionUtils.computeReturnObject(1, ((Boolean) result).booleanValue(), 0, 0.0, null);

    } else if (result.getClass() == Integer.class || result.getClass() == int.class || result.getClass() == long.class) {
      return (Long) result;

    } else if (result.getClass() == double.class) {
      return (Double) result;
    }

    // String/Object
    return result;

  }
}
