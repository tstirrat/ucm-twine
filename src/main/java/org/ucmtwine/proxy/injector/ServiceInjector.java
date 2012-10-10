package org.ucmtwine.proxy.injector;

import intradoc.data.DataException;
import intradoc.server.Action;
import intradoc.server.ServiceData;
import intradoc.server.ServiceManager;
import intradoc.shared.FilterImplementor;

import java.lang.reflect.Method;

import org.ucmtwine.annotation.ServiceMethod;
import org.ucmtwine.proxy.ServiceProxy;

public class ServiceInjector extends BaseInjector implements FilterImplementor {

  /**
   * {@inheritDoc}
   */
  @Override
  public void injectClasses(String propertiesFile) {
    injectClasses(propertiesFile, "ucm.service");
  }

  /**
   * Inject a service into the service manager registry.
   * 
   * @param klass
   *          Class to inject
   * @throws DataException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @Override
  public void inject(Class<?> klass) throws DataException {

    Method[] classMethods = klass.getMethods();

    for (Method method : classMethods) {
      ServiceMethod annotation = (ServiceMethod) method.getAnnotation(ServiceMethod.class);

      if (annotation != null) {
        injectServiceMethod(method, annotation);
      }
    }
  }

  /**
   * Injects a single UCMService annotation into the ServiceManager registry.
   * 
   * @param className
   *          Fully qualified class name
   * @param annotation
   *          A reference to a UCMService annotation
   * @throws DataException
   */
  private static void injectServiceMethod(Method method, ServiceMethod annotation) throws DataException {
    ServiceData serviceData = new ServiceData();

    String template = annotation.template();
    int accessLevel = annotation.accessLevel();
    String serviceType = (!annotation.type().equals("")) ? annotation.type() : null;
    String errorMessage = annotation.errorMessage();
    String subjects = annotation.subjects();
    String serviceName = annotation.name();

    try {
      serviceData.init(serviceName, ServiceProxy.class.getName(), accessLevel, template, serviceType, errorMessage,
          subjects);
    } catch (Exception e) {
      throw new DataException("Cannot create ServiceData object for " + serviceName + " - " + e.getMessage());
    }

    // action parameters, none by default
    String controlFlags = "";

    String methodHashCode = MethodRegistry.addMethod(method);

    if (methodHashCode == null) {
      throw new DataException("Cannot register method " + method.toString() + " because it has a null hashCode");
    }

    try {
      serviceData.addAction(Action.CODE_TYPE, "delegateWithParameters", methodHashCode, controlFlags, "");

    } catch (DataException e) {
      throw new DataException("Cannot add defaut action to service" + serviceName + " - " + e.getMessage());
    }

    // inject service
    ServiceManager.putService(serviceName, serviceData);
  }
}
