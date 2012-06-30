package org.stirrat.twine.test.parameter.types;

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
import org.stirrat.twine.parameter.IParameter;
import org.stirrat.twine.parameter.types.IntegerParameter;

@RunWith(MockitoJUnitRunner.class)
public class TestIntegerParameter {

  private DataBinder testBinder;

  @Mock
  private Service testService;

  @Before
  public void setUp() throws DataException, ServiceException {
    testBinder = new DataBinder();

    testBinder.putLocal("emptyParam", "");
    testBinder.putLocal("zeroParam", "0");
    testBinder.putLocal("oneParam", "1");
    testBinder.putLocal("invalidParam", "sdsd");
    testBinder.putLocal("negativeParam", "-1");
    testBinder.putLocal("positiveParam", "200");

    when(testService.getBinder()).thenReturn(testBinder);
  }

  @Test
  public void testGetValueFromBinderIsCorrectType() {
    assertEquals(1, new IntegerParameter("oneParam").getBinderValue(testService));
  }

  @Test
  public void testParsesStringValuesIntoIntegers() {
    assertEquals(0, new IntegerParameter("zeroParam").getBinderValue(testService));
    assertEquals(1, new IntegerParameter("oneParam").getBinderValue(testService));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyStringValueThrowsException() {
    @SuppressWarnings("unused")
    Integer temp = (Integer) new IntegerParameter("emptyParam").getBinderValue(testService);
  }

  @Test
  public void testParsesIntegerValues() {
    assertEquals(1, new IntegerParameter("oneParam").getBinderValue(testService));
    assertEquals(0, new IntegerParameter("zeroParam").getBinderValue(testService));
    assertEquals(-1, new IntegerParameter("negativeParam").getBinderValue(testService));
    assertEquals(200, new IntegerParameter("positiveParam").getBinderValue(testService));
  }

  @Test
  public void testParsesNegativeValues() {
    assertEquals(-1, new IntegerParameter("negativeParam").getBinderValue(testService));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsesInvalidStringValuesAsFalse() {
    @SuppressWarnings("unused")
    Integer temp = (Integer) new IntegerParameter("invalidParam").getBinderValue(testService);
  }

  @Test
  public void testParsesLongCorrectly() {
    IParameter p = new IntegerParameter();
    assertEquals(1, p.getArgumentValue(new Long(1L), testService));
    assertEquals(0, p.getArgumentValue(new Long(0L), testService));
    assertEquals(2, p.getArgumentValue(new Long(2L), testService));
    assertEquals(-1, p.getArgumentValue(new Long(-1L), testService));
  }

  @Test
  public void testParsesStringCorrectly() {
    IParameter p = new IntegerParameter();
    assertEquals(1, p.getArgumentValue("1", testService));
    assertEquals(0, p.getArgumentValue("0", testService));
    assertEquals(200, p.getArgumentValue("200", testService));
    assertEquals(-1, p.getArgumentValue("-1", testService));
  }

  @Test
  public void testParsesNullAsNull() {
    IParameter p = new IntegerParameter();
    assertNull(p.getArgumentValue(null, testService));
  }
}
