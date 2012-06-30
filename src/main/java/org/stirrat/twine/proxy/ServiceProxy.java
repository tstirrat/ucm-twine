package org.stirrat.twine.proxy;

import intradoc.common.ServiceException;
import intradoc.data.DataException;
import intradoc.server.Service;

import java.lang.reflect.Method;
import java.util.Vector;

import org.stirrat.twine.parameter.ParameterMarshaller;
import org.stirrat.twine.proxy.injector.MethodRegistry;

public class ServiceProxy extends Service {

  public void delegateWithParameters() throws DataException, ServiceException {

    try {

      @SuppressWarnings("unchecked")
      Vector<String> actionParams = (Vector<String>) this.m_currentAction.getParams().clone();

      // first param is the unique identifier to get the Method to run
      String methodID = actionParams.remove(0);

      Method m = MethodRegistry.getMethod(methodID);

      ParameterMarshaller marshaller = new ParameterMarshaller(m);

      Object[] params = marshaller.getValueArray(this);

      Object context = m.getDeclaringClass().newInstance();

      m.invoke(context, params);

    } catch (IllegalArgumentException e) {

      throw new DataException(e.getMessage(), e);

    } catch (Exception e) {

      throw new ServiceException(e.getMessage(), e);
    }
  }
}
