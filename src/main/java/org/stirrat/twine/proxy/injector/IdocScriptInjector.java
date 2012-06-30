package org.stirrat.twine.proxy.injector;

import intradoc.common.AppObjectRepository;
import intradoc.common.ScriptContext;
import intradoc.common.ScriptExtensions;
import intradoc.common.ServiceException;
import intradoc.data.DataException;
import intradoc.shared.FilterImplementor;

import org.stirrat.twine.proxy.ScriptProxy;

public class IdocScriptInjector extends BaseInjector implements IClassInjector, FilterImplementor {

  /**
   * {@inheritDoc}
   */
  @Override
  public void injectClasses(String propertiesFile) {
    injectClasses(propertiesFile, "ucm.idocscript");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void inject(Class<?> klass) throws DataException, ServiceException {

    // loads function def table from annotated class
    ScriptExtensions extensions = new ScriptProxy(klass);

    // load func def table and set parent to the default script context
    ScriptContext defaultScriptContext = (ScriptContext) AppObjectRepository.getObject("DefaultScriptContext");
    extensions.load(defaultScriptContext);

    // register these extensions
    defaultScriptContext.registerExtension(extensions);
  }
}
