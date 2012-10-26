package org.ucmtwine;

import intradoc.common.ExecutionContext;
import intradoc.common.ServiceException;
import intradoc.common.SystemUtils;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.Workspace;
import intradoc.shared.FilterImplementor;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

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
}
