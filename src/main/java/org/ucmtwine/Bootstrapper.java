package org.ucmtwine;

import java.util.Vector;

import org.ucmtwine.proxy.injector.FilterInjector;
import org.ucmtwine.proxy.injector.IClassInjector;
import org.ucmtwine.proxy.injector.IdocScriptInjector;
import org.ucmtwine.proxy.injector.ServiceInjector;

import intradoc.common.ExecutionContext;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.Workspace;
import intradoc.shared.FilterImplementor;
import intradoc.shared.PluginFilterData;
import intradoc.shared.PluginFilters;

public class Bootstrapper implements FilterImplementor {

  /**
   * Begins injection of filters, including the Service and IdocScript injectors
   * required to load other ucm entities.
   */
  public int doFilter(Workspace ws, DataBinder binder, ExecutionContext ctx) throws DataException, ServiceException {
    String propertiesFile = (String) ctx.getCachedObject("filterParameter");

    // only bootstrap if a valid properties file is supplied.
    if (propertiesFile != null && !propertiesFile.equals("")) {
      
      IClassInjector filterInjector = new FilterInjector();
      filterInjector.injectClasses(propertiesFile);

      loadServiceFilter(propertiesFile);
      loadIdocScriptFilter(propertiesFile);
    }

    return CONTINUE;
  }

  /**
   * Sets up the filter to inject Idoc Scripts.
   * 
   * @param propertiesFile
   */
  private void loadIdocScriptFilter(String propertiesFile) {
    // TODO This should use a singleton and only one injector with multiple
    // properties files.
    PluginFilterData filter = new PluginFilterData();

    filter.m_order = 100;
    filter.m_parameter = propertiesFile;
    filter.m_filterType = "extraAfterServicesLoadInit";
    filter.m_location = IdocScriptInjector.class.getName();

    Vector<PluginFilterData> filters = new Vector<PluginFilterData>();
    filters.add(filter);

    PluginFilters.registerFilters(filters);
  }

  /**
   * Sets up the filter to inject services.
   * 
   * @param propertiesFile
   */
  private void loadServiceFilter(String propertiesFile) {
    // TODO This should use a singleton and only one injector with multiple
    // properties files.
    PluginFilterData filter = new PluginFilterData();

    filter.m_order = 1;
    filter.m_parameter = propertiesFile;
    filter.m_filterType = "extraAfterServicesLoadInit";
    filter.m_location = ServiceInjector.class.getName();

    Vector<PluginFilterData> filters = new Vector<PluginFilterData>();
    filters.add(filter);

    PluginFilters.registerFilters(filters);
  }
}
