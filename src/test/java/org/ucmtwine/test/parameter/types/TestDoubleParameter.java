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
import org.ucmtwine.parameter.types.DoubleParameter;

@RunWith(MockitoJUnitRunner.class)
public class TestDoubleParameter {

  private DataBinder testBinder;

  @Mock
  private Service testService;

  @Before
  public void setUp() throws DataException, ServiceException {
    testBinder = new DataBinder();

    testBinder.putLocal("emptyParam", "");
    testBinder.putLocal("zeroParam", "0.0");
    testBinder.putLocal("oneParam", "1.0");
    testBinder.putLocal("invalidParam", "sdsd");
    testBinder.putLocal("negativeParam", "-1.0");
    testBinder.putLocal("positiveParam", "200.0");

    when(testService.getBinder()).thenReturn(testBinder);
  }

  @Test
  public void testGetValueFromBinderIsCorrectType() {
    assertEquals(1.0, new DoubleParameter("oneParam").getBinderValue(testService));
  }

  @Test
  public void testParsesStringValuesIntoIntegers() {
    assertEquals(0.0, new DoubleParameter("zeroParam").getBinderValue(testService));
    assertEquals(1.0, new DoubleParameter("oneParam").getBinderValue(testService));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyStringValueThrowsException() {
    @SuppressWarnings("unused")
    Double temp = (Double) new DoubleParameter("emptyParam").getBinderValue(testService);
  }

  @Test
  public void testParsesIntegerValues() {
    assertEquals(1.0, new DoubleParameter("oneParam").getBinderValue(testService));
    assertEquals(0.0, new DoubleParameter("zeroParam").getBinderValue(testService));
    assertEquals(-1.0, new DoubleParameter("negativeParam").getBinderValue(testService));
    assertEquals(200.0, new DoubleParameter("positiveParam").getBinderValue(testService));
  }

  @Test
  public void testParsesNegativeValues() {
    assertEquals(-1.0, new DoubleParameter("negativeParam").getBinderValue(testService));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsesInvalidStringValuesAsFalse() {
    @SuppressWarnings("unused")
    Double temp = (Double) new DoubleParameter("invalidParam").getBinderValue(testService);
  }

  @Test
  public void testParsesIdocScriptDoubleCorrectly() {
    IParameter p = new DoubleParameter();
    assertEquals(1.0, p.getArgumentValue(new Double(1L), testService));
    assertEquals(0.0, p.getArgumentValue(new Double(0L), testService));
    assertEquals(2.0, p.getArgumentValue(new Double(2L), testService));
    assertEquals(-1.0, p.getArgumentValue(new Double(-1L), testService));
  }

  @Test
  public void testParsesStringCorrectly() {
    IParameter p = new DoubleParameter();
    assertEquals(1.0, p.getArgumentValue("1", testService));
    assertEquals(0.0, p.getArgumentValue("0", testService));
    assertEquals(200.0, p.getArgumentValue("200", testService));
    assertEquals(-1.0, p.getArgumentValue("-1", testService));

    assertEquals(1.0, p.getArgumentValue("1.0", testService));
    assertEquals(0.0, p.getArgumentValue("0.0", testService));
    assertEquals(200.1, p.getArgumentValue("200.1", testService));
    assertEquals(-1.0, p.getArgumentValue("-1.0", testService));
  }

  @Test
  public void testParsesNullAsNull() {
    IParameter p = new DoubleParameter();
    assertNull(p.getArgumentValue(null, testService));
  }
}
