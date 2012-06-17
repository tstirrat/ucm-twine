package org.stirrat.ecm.annotations.test.parameter.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import intradoc.common.ExecutionContext;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.Workspace;
import intradoc.server.HttpImplementor;
import intradoc.server.PageMerger;
import intradoc.server.Service;
import intradoc.server.ServiceRequestImplementor;
import intradoc.shared.UserData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.stirrat.twine.parameter.Parameter;
import org.stirrat.twine.parameter.types.InjectedParameter;

@RunWith(MockitoJUnitRunner.class)
public class TestInjectedParameter {

  @Mock
  private Service testService;

  private DataBinder testBinder;

  @Before
  public void setUp() throws DataException, ServiceException {
    testBinder = new DataBinder();
    when(testService.getBinder()).thenReturn(testBinder);
  }

  @Test
  public void testBinderIsValidType() {
    assertTrue(InjectedParameter.isValidType(DataBinder.class));

    assertTrue(InjectedParameter.isValidType(ExecutionContext.class));

    assertTrue(InjectedParameter.isValidType(Service.class));

    assertTrue(InjectedParameter.isValidType(PageMerger.class));

    assertTrue(InjectedParameter.isValidType(HttpImplementor.class));

    assertTrue(InjectedParameter.isValidType(UserData.class));

    assertTrue(InjectedParameter.isValidType(ServiceRequestImplementor.class));

    assertTrue(InjectedParameter.isValidType(Workspace.class));

    assertTrue(InjectedParameter.isValidType(DataBinder.class));
  }

  @Test
  public void testBinderIsReturned() throws IllegalAccessException {
    assertTrue(InjectedParameter.isValidType(DataBinder.class));

    InjectedParameter p = (InjectedParameter) Parameter.create(DataBinder.class);

    Object val = p.getBinderValue(testService);

    assertEquals(DataBinder.class, val.getClass());
  }
}
