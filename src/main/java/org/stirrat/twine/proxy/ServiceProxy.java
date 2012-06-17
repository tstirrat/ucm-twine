package org.stirrat.twine.proxy;

import intradoc.common.ServiceException;
import intradoc.data.DataException;
import intradoc.server.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stirrat.twine.parameter.ParameterMarshaller;

public class ServiceProxy extends Service {

  private static Map<String, Method> methods;

  @SuppressWarnings("unused")
  private static Logger log = LoggerFactory.getLogger(ServiceProxy.class);

  public void delegateWithParameters() throws DataException, ServiceException {

    try {

      @SuppressWarnings("unchecked")
      Vector<String> actionParams = (Vector<String>) this.m_currentAction.getParams().clone();

      // first param is the unique identifier to get the Method to run
      String methodID = actionParams.remove(0);

      Method m = ServiceProxy.getMethod(methodID);

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
