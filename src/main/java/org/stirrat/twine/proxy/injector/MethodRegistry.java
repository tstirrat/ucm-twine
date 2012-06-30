package org.stirrat.twine.proxy.injector;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MethodRegistry {

  private static HashMap<String, Method> methods;

  /**
   * Adds a method to the registry and returns the UUID.
   * 
   * @return
   */
  public static String addMethod(Method m) {
    if (methods == null) {
      methods = new HashMap<String, Method>();
    }

    String hashCode = String.valueOf(m.hashCode());

    if (!methods.containsKey(hashCode)) {
      methods.put(hashCode, m);
    }

    return String.valueOf(m.hashCode());
  }

  public static Method getMethod(String methodID) {

    if (methods == null)
      return null;

    return methods.get(methodID);
  }
}
