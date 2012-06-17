package org.stirrat.twine.proxy.injector;

import intradoc.data.DataException;
import intradoc.server.Action;
import intradoc.server.ServiceData;
import intradoc.server.ServiceManager;
import intradoc.shared.FilterImplementor;

import java.io.IOException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.stirrat.twine.annotation.ServiceMethod;
import org.stirrat.twine.proxy.ServiceProxy;

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

    // method, pType,pName,pOptional,...
    String parameters = createActionParameters(method);

    try {
      serviceData.addAction(Action.CODE_TYPE, "delegateWithParameters", parameters, controlFlags,
          "Error executing delegateWithParameters()");
    } catch (DataException e) {
      throw new DataException("Cannot add defaut action to service" + serviceName + " - " + e.getMessage());
    }

    // inject service
    ServiceManager.putService(serviceName, serviceData);
  }

  /**
   * Creates the Action parameter list which contains the parameter definition
   * and entry point method.
   * 
   * @param method
   *          The UCMService entry point method.
   * @return A string to put into the action parameter
   * @throws DataException
   * @throws ClassNotFoundException
   * @throws IllegalArgumentException
   */
  private static String createActionParameters(Method method) throws DataException {

    String id = ServiceProxy.addMethod(method);

    if (id == null) {
      throw new DataException("Cannot register method " + method.toString());
    }
    // try {
    // ParameterMarshaller marshaller = new ParameterMarshaller(method);
    //
    // return method.getName() + "," + marshaller.toActionString();
    //
    // } catch (IllegalArgumentException e) {
    // throw new DataException(e.getMessage());
    //
    // }

    return id;
  }

  /**
   * Return a proxied POJO which inherits a super class.
   * 
   * @param subclass
   * @param superclass
   * @return
   * @throws NotFoundException
   * @throws CannotCompileException
   * @throws IOException
   */
  public static Class<?> getProxiedClass(Class<?> subclass, Class<?> superclass) throws CannotCompileException,
      NotFoundException, IOException {
    // allready is one.
    if (superclass.isAssignableFrom(subclass)) {
      return subclass;
    }

    ClassPool pool = ClassPool.getDefault();
    CtClass cc = pool.get(subclass.getName());
    cc.setSuperclass(pool.get(superclass.getName()));
    cc.writeFile();

    return cc.toClass();
  }
}
