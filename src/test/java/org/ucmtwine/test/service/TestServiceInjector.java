package org.ucmtwine.test.service;

import static org.mockito.Mockito.when;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.server.Service;

import org.junit.Before;
import org.mockito.Mock;

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
}
