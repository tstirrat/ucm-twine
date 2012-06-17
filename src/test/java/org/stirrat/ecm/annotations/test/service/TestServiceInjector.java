package org.stirrat.ecm.annotations.test.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.server.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.junit.Before;
import org.mockito.Mock;
import org.stirrat.twine.proxy.ServiceProxy;
import org.stirrat.twine.proxy.injector.ServiceInjector;

// @RunWith(MockitoJUnitRunner.class)
public class TestServiceInjector {

  private DataBinder testBinder;

  @Mock
  private Service testService;

  @Before
  public void setUp() throws DataException, ServiceException {
    testBinder = new DataBinder();

    testBinder.putLocal("empty", "");
    testBinder.putLocal("timestamp", "");
    testBinder.putLocal("one", "1");
    testBinder.putLocal("enAU", "");
    testBinder.putLocal("negativeParam", "-1");
    testBinder.putLocal("positiveParam", "200");

    when(testService.getBinder()).thenReturn(testBinder);
  }

  
  public void test() throws SecurityException, NoSuchMethodException, CannotCompileException, NotFoundException,
      IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Class<?> c = ServiceInjector.getProxiedClass(ExampleService.class, ServiceProxy.class);

    Object o = c.newInstance();

    Method methods[] = o.getClass().getMethods();

    Method superMethod = null;
    String superMethodName = "delegateWithParameters";

    for (Method m : methods) {
      if (m.getName().equalsIgnoreCase(superMethodName)) {
        superMethod = m;
      }
    }

    assertNotNull("Must have super method of " + superMethodName, superMethod);
  }

}
