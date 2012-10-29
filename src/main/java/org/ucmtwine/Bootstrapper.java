package org.ucmtwine;

import intradoc.common.ExecutionContext;
import intradoc.common.ServiceException;
import intradoc.common.SystemUtils;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.Workspace;
import intradoc.io.zip.IdcZipFile;
import intradoc.shared.FilterImplementor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ucmtwine.proxy.injector.FilterInjector;
import org.ucmtwine.proxy.injector.IClassInjector;
import org.ucmtwine.proxy.injector.IdocScriptInjector;
import org.ucmtwine.proxy.injector.ServiceInjector;

public class Bootstrapper implements FilterImplementor {

  /**
   * Begins injection of filters, including the Service and IdocScript injectors
   * required to load other ucm entities.
   */
  public int doFilter(Workspace ws, DataBinder binder, ExecutionContext ctx) throws DataException, ServiceException {
    String configFileName = (String) ctx.getCachedObject("filterParameter");

    try {
      ClassLoader cl = getClass().getClassLoader();
      Enumeration<URL> propFiles = cl.getResources(configFileName);

      // There should be at least one entry (TwineLib contains an example file)
      if (!propFiles.hasMoreElements()) {
        propFiles = getResources11g(cl, configFileName);
      }

      while (propFiles.hasMoreElements()) {
        URL propFile = propFiles.nextElement();

        if (SystemUtils.m_verbose) {
          SystemUtils.trace("twine", "Loading config file: " + propFile.toString());
        }

        IClassInjector filterInjector = new FilterInjector();
        filterInjector.injectClasses(propFile);

        IClassInjector serviceInjector = new ServiceInjector();
        serviceInjector.injectClasses(propFile);

        IClassInjector scriptInjector = new IdocScriptInjector();
        scriptInjector.injectClasses(propFile);

      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return CONTINUE;
  }

  /**
   * Replacement for getResources which works on 11g.
   * 
   * <p>
   * The UCM 11G {@link ClassLoader} does not load component jars in the
   * standard manner, which means you cannot use
   * {@link ClassLoader#getResources(String)} to find all instances of a
   * specific file in the jar path, instead we inspect the m_zipfiles map, where
   * we can query each zip file for the properties file.
   * </p>
   * 
   * <p>
   * Instead, we use reflection to find all the loaded component jar files and
   * search manually
   * </p>
   * 
   * @param classLoader
   * @param configFileName
   * @throws MalformedURLException
   */
  private Enumeration<URL> getResources11g(ClassLoader classLoader, String configFileName) {
    List<URL> newProps = new ArrayList<URL>();
    if (classLoader.getClass().getSimpleName().equalsIgnoreCase("IdcClassLoader")) {
      try {
        Field field = classLoader.getClass().getField("m_zipfiles");

        @SuppressWarnings("unchecked")
        Map<String, IdcZipFile> zipFiles = (Map<String, IdcZipFile>) field.get(classLoader);

        for (Entry<String, IdcZipFile> entry : zipFiles.entrySet()) {
          if (entry.getValue().m_entries.get(configFileName) != null) {
            String jarFile = entry.getKey();

            // windows needs a slash before the C:/
            if (!jarFile.startsWith("/")) {
              jarFile = "/" + jarFile;
            }

            try {
              URL u = new URL("jar:file:" + entry.getKey() + "!/" + configFileName);
              newProps.add(u);

            } catch (MalformedURLException e) {
              e.printStackTrace();
            }
          }
        }

      } catch (Exception e) {
        // If there is any exception the ClassLoader is an unrecognised format
        e.printStackTrace();
      }
    }
    return Collections.enumeration(newProps);
  }
}
