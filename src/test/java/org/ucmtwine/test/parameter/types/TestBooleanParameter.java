package org.ucmtwine.test.parameter.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.server.Service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.ucmtwine.parameter.IParameter;
import org.ucmtwine.parameter.types.BooleanParameter;

@RunWith(MockitoJUnitRunner.class)
public class TestBooleanParameter {

  private IParameter primitiveParam;
  private IParameter objectParam;

  private DataBinder testBinder;

  @Mock
  private Service testService;

  @Before
  public void setUp() throws DataException, ServiceException {
    primitiveParam = new BooleanParameter("trueParam", boolean.class);
    objectParam = new BooleanParameter("trueParam", Boolean.class);

    testBinder = new DataBinder();

    testBinder.putLocal("trueParam", "true");
    testBinder.putLocal("falseParam", "false");
    testBinder.putLocal("emptyParam", "");
    testBinder.putLocal("zeroParam", "0");
    testBinder.putLocal("oneParam", "1");
    testBinder.putLocal("invalidParam", "sdsd");
    testBinder.putLocal("capitalParam", "TRUE");
    testBinder.putLocal("negativeParam", "-1");
    testBinder.putLocal("integerParam", "200");

    when(testService.getBinder()).thenReturn(testBinder);
  }

  @Test
  public void testGetValueFromBinderIsABoolean() throws IllegalAccessException {
    assertEquals(true, primitiveParam.getBinderValue(testService));
    assertEquals(true, objectParam.getBinderValue(testService));
  }

  @Test
  public void testParsesStringValuesIntoBooleans() throws IllegalAccessException {
    assertEquals(true, new BooleanParameter("trueParam", boolean.class).getBinderValue(testService));
    assertEquals(false, new BooleanParameter("falseParam", boolean.class).getBinderValue(testService));
    assertEquals(true, new BooleanParameter("capitalParam", boolean.class).getBinderValue(testService));
  }

  @Test
  public void testParsesEmptyValueAsFalse() throws IllegalAccessException {
    assertEquals(false, new BooleanParameter("emptyParam", boolean.class).getBinderValue(testService));
  }

  @Test
  public void testParsesIntegerValues() throws IllegalAccessException {
    assertEquals(true, new BooleanParameter("oneParam", boolean.class).getBinderValue(testService));
    assertEquals(false, new BooleanParameter("zeroParam", boolean.class).getBinderValue(testService));
  }

  @Test
  public void testParsesNegativeValuesAsFalse() throws IllegalAccessException {
    assertEquals(false, new BooleanParameter("negativeParam", boolean.class).getBinderValue(testService));
  }

  @Test
  public void testParsesEmptyValuesAsFalse() throws IllegalAccessException {
    assertEquals(false, new BooleanParameter("emptyParam", boolean.class).getBinderValue(testService));
  }

  @Test
  public void testParsesInvalidStringValuesAsFalse() throws IllegalAccessException {
    assertEquals(false, new BooleanParameter("invalidParam", boolean.class).getBinderValue(testService));
  }

  @Test
  public void testParsesIntegerValuesAboveOneAsFalse() throws IllegalAccessException {
    assertEquals(false, new BooleanParameter("integerParam", boolean.class).getBinderValue(testService));
  }

  @Test
  public void testParsesLongAsBoolean() throws IllegalAccessException {
    IParameter p = new BooleanParameter();
    assertEquals(true, p.getArgumentValue(new Long(1L), testService));
    assertEquals(false, p.getArgumentValue(new Long(0L), testService));
    assertEquals(false, p.getArgumentValue(new Long(2L), testService));
    assertEquals(false, p.getArgumentValue(new Long(-1L), testService));
  }

  @Test
  public void testParsesStringAsBoolean() throws IllegalAccessException {
    IParameter p = new BooleanParameter();
    assertEquals(true, p.getArgumentValue("true", testService));
    assertEquals(false, p.getArgumentValue("false", testService));
    assertEquals(false, p.getArgumentValue("", testService));
    assertEquals(false, p.getArgumentValue("-1", testService));
  }

  @Test
  public void testParsesBooleanAsBoolean() throws IllegalAccessException {
    IParameter p = new BooleanParameter();
    assertEquals(true, p.getArgumentValue(new Boolean(true), testService));
    assertEquals(false, p.getArgumentValue(new Boolean(false), testService));
    assertEquals(true, p.getArgumentValue(true, testService));
    assertEquals(false, p.getArgumentValue(false, testService));
  }

  @Test
  public void testParsesNullAsNull() throws IllegalAccessException {
    IParameter p = new BooleanParameter(boolean.class);
    assertNull(p.getArgumentValue(null, testService));
  }
}
