package org.stirrat.twine.proxy;

import intradoc.common.ExecutionContext;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.Workspace;
import intradoc.shared.FilterImplementor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.stirrat.twine.parameter.ParameterMarshaller;

public class FilterProxy implements FilterImplementor {

  private static Map<String, Method> methods;

  /**
   * Main entry point which will delegate to the filter method with dependency
   * injection.
   */
  public int doFilter(Workspace ws, DataBinder binder, ExecutionContext ctx) throws DataException, ServiceException {

    Object returnVal = null;

    try {

      String methodID = (String) ctx.getCachedObject("filterParameter");

      Method m = getMethod(methodID);

      ParameterMarshaller marshaller = new ParameterMarshaller(m);

      Object[] params = marshaller.getValueArray(ws, binder, ctx);

      Object context = m.getDeclaringClass().newInstance();

      returnVal = m.invoke(context, params);

    } catch (IllegalArgumentException e) {

      throw new DataException(e.getMessage(), e);

    } catch (Exception e) {

      throw new ServiceException(e.getMessage(), e);
    }

    if (returnVal != null && returnVal instanceof Integer) {
      return ((Integer) returnVal).intValue();
    }

    return CONTINUE;
  }

  /**
   * Adds a method to the registry and returns the UUID.
   * 
   * @return
   */
  public static String addMethod(Method m) {
    if (methods == null) {
      methods = new HashMap<String, Method>();
    }

    // TODO: needs duplicate check

    Long uuid = UUID.randomUUID().getMostSignificantBits();

    String uuidString = Long.toHexString(uuid);

    methods.put(uuidString, m);

    return uuidString;
  }

  public static Method getMethod(String methodID) {

    if (methods == null)
      return null;

    return methods.get(methodID);
  }

}
