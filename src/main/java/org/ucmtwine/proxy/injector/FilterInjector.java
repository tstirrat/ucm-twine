package org.ucmtwine.proxy.injector;

import intradoc.data.DataException;
import intradoc.shared.PluginFilterData;
import intradoc.shared.PluginFilters;

import java.lang.reflect.Method;
import java.util.Vector;

import org.ucmtwine.annotation.Filter;
import org.ucmtwine.proxy.FilterProxy;

/**
 * Injects classes as UCM filters. (This filter is itself a filter, but should
 * not be annotated nor injected using itself)
 * 
 * @author tim
 */
public class FilterInjector extends BaseInjector implements IClassInjector {

  /**
   * {@inheritDoc}
   */
  @Override
  public void injectClasses(String propertiesFile) {
    injectClasses(propertiesFile, "ucm.filter");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void inject(Class<?> klass) throws DataException {

    Method[] classMethods = klass.getMethods();

    for (Method method : classMethods) {
      Filter annotation = (Filter) method.getAnnotation(Filter.class);

      if (annotation != null) {
        injectFilterMethod(method, annotation);
      }
    }
  }

  /**
   * 
   * @param method
   * @param annotation
   * @throws DataException
   */
  private void injectFilterMethod(Method method, Filter annotation) throws DataException {

    if (annotation == null) {
      throw new DataException("Method [" + method.getName() + "] is not an injectable filter.");
    }

    // TODO: is this necessary?
    if (method.getReturnType() != int.class && method.getReturnType() != void.class) {
      throw new DataException("Method [" + method.getName()
          + "] must have a return type of int or void to be a filter.");
    }

    String methodID = MethodRegistry.addMethod(method);

    if (methodID == null) {
      throw new DataException("Could not generate UUID for method " + method.getName());
    }

    Vector<PluginFilterData> filters = new Vector<PluginFilterData>();

    PluginFilterData filter = new PluginFilterData();

    filter.m_order = annotation.loadOrder();
    filter.m_parameter = methodID;
    filter.m_filterType = annotation.event();
    filter.m_location = FilterProxy.class.getName();

    filters.add(filter);

    PluginFilters.registerFilters(filters);
  }
}
