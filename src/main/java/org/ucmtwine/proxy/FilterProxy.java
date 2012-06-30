package org.ucmtwine.proxy;

import intradoc.common.ExecutionContext;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.Workspace;
import intradoc.shared.FilterImplementor;

import java.lang.reflect.Method;

import org.ucmtwine.parameter.ParameterMarshaller;
import org.ucmtwine.proxy.injector.MethodRegistry;

public class FilterProxy implements FilterImplementor {

  /**
   * Main entry point which will delegate to the filter method with dependency
   * injection.
   */
  public int doFilter(Workspace ws, DataBinder binder, ExecutionContext ctx) throws DataException, ServiceException {

    Object returnVal = null;

    try {

      String methodID = (String) ctx.getCachedObject("filterParameter");

      Method m = MethodRegistry.getMethod(methodID);

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
}
