package org.stirrat.twine.proxy.injector;

import intradoc.common.AppObjectRepository;
import intradoc.common.ScriptContext;
import intradoc.common.ScriptExtensions;
import intradoc.common.ServiceException;
import intradoc.data.DataException;
import intradoc.shared.FilterImplementor;

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

    ScriptContext defaultScriptContext = (ScriptContext) AppObjectRepository.getObject("DefaultScriptContext");

    ScriptExtensions extensions;
    try {
      extensions = (ScriptExtensions) klass.newInstance();

      extensions.load(defaultScriptContext);

      defaultScriptContext.registerExtension(extensions);

    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
