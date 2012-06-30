package org.ucmtwine.proxy.injector;

import intradoc.common.ServiceException;
import intradoc.data.DataException;

import java.util.List;

/**
 * Enumerates a list of classes from a properties file for injection.
 * 
 * @author tim
 */
public interface IClassInjector {

  /**
   * Enumerate classes from a properties file for injection.
   * 
   * @param propertiesFile
   *          The relative path and name of a properties file
   * @param prefix
   *          The prefix to look for when enumerating classes
   * @return The list of checked classes
   */
  List<Class<?>> enumerateClasses(String propertiesFile, String prefix);

  /**
   * Inject classes which are defined in a properties file.
   * 
   * @param propertiesFile
   *          The relative path and name of a properties file
   */
  void injectClasses(String propertiesFile);

  /**
   * Inject classes which are defined in a properties file with a given prefix.
   * 
   * @param propertiesFile
   *          The relative path and name of a properties file
   * @param prefix
   *          The prefix to look for when enumerating classes
   */
  void injectClasses(String propertiesFile, String prefix);

  /**
   * Inject the enumerated classes via the subclass implemented method.
   * 
   * @param classes
   *          An array of valid classes to inject.
   */
  void injectClasses(List<Class<?>> classes);

  /**
   * Inject a single class.
   * 
   * @param klass
   *          The class to inject
   * @throws DataException
   * @throws ServiceException
   */
  void inject(Class<?> klass) throws DataException, ServiceException;
}
