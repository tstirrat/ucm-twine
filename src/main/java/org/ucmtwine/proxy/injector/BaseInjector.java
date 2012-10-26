package org.ucmtwine.proxy.injector;

import intradoc.common.Log;
import intradoc.common.ServiceException;
import intradoc.common.SystemUtils;
import intradoc.data.DataException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class BaseInjector implements IClassInjector {

  /**
   * {@inheritDoc}
   */
  public List<Class<?>> enumerateClasses(URL propertiesFile, String prefix) {
    // find mapped service classes in service.properties
    Properties properties = new Properties();

    InputStream inputStream = null;
    try {
      inputStream = propertiesFile.openStream();

      if (inputStream != null) {
        try {
          properties.load(inputStream);
        } catch (IOException ioe) {
          Log.warn(ioe.getMessage());
        }
      }
    } catch (IOException e) {
      Log.warn(e.getMessage());
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
  public void injectClasses(URL propertiesFile, String prefix) {
    List<Class<?>> classes = enumerateClasses(propertiesFile, prefix);
    injectClasses(classes);
  }

  /**
   * {@inheritDoc}
   */
  public abstract void injectClasses(URL propertiesFile);

  /**
   * {@inheritDoc}
   */
  public abstract void inject(Class<?> klass) throws DataException, ServiceException;
}
