package org.ucmtwine.parameter.types;

import intradoc.common.ExecutionContext;
import intradoc.data.DataBinder;
import intradoc.data.Workspace;
import intradoc.server.HttpImplementor;
import intradoc.server.PageMerger;
import intradoc.server.Service;
import intradoc.server.ServiceRequestImplementor;
import intradoc.shared.UserData;

import org.ucmtwine.parameter.Parameter;

/**
 * Place-holder parameter which doesn't retrieve it's value from the binder,
 * rather is injected when enumerating the parameter list before invocation.
 * 
 * @author tim
 */
public class InjectedParameter extends Parameter {

  public InjectedParameter(String name, Class<?> type) {
    super(name, type);
  }

  /**
   * Returns null because the type should be injected rather than retrieved.
   * This is a place-holder not a value retrieval.
   * 
   * @throws IllegalAccessException
   */
  @Override
  public Object getBinderValue(Service service) throws IllegalAccessException {
    return getInjectedValue(service);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getArgumentValue(Object object, Service service) throws ClassCastException {
    return getInjectedValue(service);
  }

  /**
   * Special case for Injectable type. Injects the required parameter from the
   * ExecutionContext (service)
   * 
   * @param service
   * @return
   */
  protected Object getInjectedValue(Service service) {

    if (type == UserData.class) {
      return service.getUserData();

    } else if (type == PageMerger.class) {
      return service.getPageMerger();

    } else if (type == DataBinder.class) {
      return service.getBinder();

    } else if (type == HttpImplementor.class) {
      return service.getHttpImplementor();

    } else if (type == ServiceRequestImplementor.class) {
      return service.getRequestImplementor();

    } else if (type == Workspace.class) {
      return service.getWorkspace();

    } else if (ExecutionContext.class.isAssignableFrom(type)) {
      return service;
    }

    throw new IllegalStateException("Injectable type " + type.getName() + " not known.");
  }

  /**
   * {@inheritDoc}
   */
  public int getGrammarElementType() {
    return -1; // ignore element type in idoc functions
  }

  public static boolean isValidType(Class<?> type) {
    return (type == UserData.class || ExecutionContext.class.isAssignableFrom(type) || type == PageMerger.class
        || type == DataBinder.class || type == HttpImplementor.class || type == ServiceRequestImplementor.class || type == Workspace.class);
  }
}
