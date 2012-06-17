package org.stirrat.twine.example;

import intradoc.common.ServiceException;
import intradoc.common.SystemUtils;
import intradoc.data.DataException;
import intradoc.shared.FilterImplementor;

import org.stirrat.twine.annotation.Filter;

/**
 * Example hello world filter.
 * 
 * @author tim
 */
public class ExampleFilter {

  /**
   * Filter Entry point.
   */
  @Filter(event = "extraAfterProvidersStartedInit")
  public int doSomething() throws DataException, ServiceException {

    SystemUtils.trace("twine", "ExampleFilter: Hello World!");

    return FilterImplementor.CONTINUE;
  }

}
