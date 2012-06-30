package org.ucmtwine.proxy.injector;

import intradoc.common.ExecutionContext;
import intradoc.common.Log;
import intradoc.common.ServiceException;
import intradoc.common.SystemUtils;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.Workspace;
import intradoc.shared.FilterImplementor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class BaseInjector implements IClassInjector, FilterImplementor {
  
  /**
   * Filter entry point.
   * 
   * @return Filter status of: finished, error or continue.
   */
  public int doFilter(Workspace ws, DataBinder binder, ExecutionContext ctx) throws DataException, ServiceException {

    String propertiesFile = (String) ctx.getCachedObject("filterParameter");

    if (propertiesFile == null || propertiesFile.equals("")) {
      propertiesFile = "ucm.properties";
    }

    injectClasses(propertiesFile);
    return CONTINUE;
  }
  
  /**
   * {@inheritDoc}
   */
  public List<Class<?>> enumerateClasses(String propertiesFile, String prefix) {
    // find mapped service classes in service.properties
    Properties properties = new Properties();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFile);
    if (inputStream != null) {
      try {
        properties.load(inputStream);
      } catch (IOException ioe) {
        Log.warn(ioe.getMessage());
      }
    }

    List<Class<?>> classes = new ArrayList<Class<?>>();

    if (properties.size() > 0) {
      for (Object key : properties.keySet()) {
        if (key.toString().startsWith(prefix)) {
          try {
            // does the class resolve?
            Class<?> klass = Class.forName(properties.get(key).toString());

            // if so, add it
            classes.add(klass);

          } catch (ClassNotFoundException e) {
            Log.warn("Unable to find class [" + properties.get(key).toString() + "]");
          }
        }
      }

    } else {
      Log.warn("Failed to load propertes file:" + propertiesFile);
    }

    return classes;
  }

  /**
   * {@inheritDoc}
   */
  public void injectClasses(List<Class<?>> classes) {
    for (Class<?> klass : classes) {
      try {
        inject(klass);
      } catch (Exception e) {
        String msg = "Failed to inject: " + klass.getName().toString() + " - " + e.getMessage();
        Log.warn(msg);
        SystemUtils.trace("twine", msg);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void injectClasses(String propertiesFile, String prefix) {
    List<Class<?>> classes = enumerateClasses(propertiesFile, prefix);
    injectClasses(classes);
  }
  
  /**
   * {@inheritDoc}
   */
  public abstract void injectClasses(String propertiesFile);

  /**
   * {@inheritDoc}
   */
  public abstract void inject(Class<?> klass) throws DataException, ServiceException;
}
