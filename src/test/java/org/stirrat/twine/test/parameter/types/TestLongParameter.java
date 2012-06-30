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
import org.stirrat.twine.parameter.types.LongParameter;

@RunWith(MockitoJUnitRunner.class)
public class TestLongParameter {

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
    assertEquals(1L, new LongParameter("oneParam").getBinderValue(testService));
  }

  @Test
  public void testParsesStringValuesIntoIntegers() {
    assertEquals(0L, new LongParameter("zeroParam").getBinderValue(testService));
    assertEquals(1L, new LongParameter("oneParam").getBinderValue(testService));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyStringValueThrowsException() {
    @SuppressWarnings("unused")
    Long temp = (Long) new LongParameter("emptyParam").getBinderValue(testService);
  }

  @Test
  public void testParsesIntegerValues() {
    assertEquals(1L, new LongParameter("oneParam").getBinderValue(testService));
    assertEquals(0L, new LongParameter("zeroParam").getBinderValue(testService));
    assertEquals(-1L, new LongParameter("negativeParam").getBinderValue(testService));
    assertEquals(200L, new LongParameter("positiveParam").getBinderValue(testService));
  }

  @Test
  public void testParsesNegativeValues() {
    assertEquals(-1L, new LongParameter("negativeParam").getBinderValue(testService));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsesInvalidStringValuesAsFalse() {
    @SuppressWarnings("unused")
    Long temp = (Long) new LongParameter("invalidParam").getBinderValue(testService);
  }

  @Test
  public void testParsesLongCorrectly() {
    IParameter p = new LongParameter();
    assertEquals(1L, p.getArgumentValue(new Long(1L), testService));
    assertEquals(0L, p.getArgumentValue(new Long(0L), testService));
    assertEquals(2L, p.getArgumentValue(new Long(2L), testService));
    assertEquals(-1L, p.getArgumentValue(new Long(-1L), testService));
  }

  @Test
  public void testParsesStringCorrectly() {
    IParameter p = new LongParameter();
    assertEquals(1L, p.getArgumentValue("1", testService));
    assertEquals(0L, p.getArgumentValue("0", testService));
    assertEquals(200L, p.getArgumentValue("200", testService));
    assertEquals(-1L, p.getArgumentValue("-1", testService));
  }

  @Test
  public void testParsesNullAsNull() {
    IParameter p = new LongParameter();
    assertNull(p.getArgumentValue(null, testService));
  }
}
