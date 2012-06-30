package org.ucmtwine.test.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import intradoc.data.DataResultSet;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ucmtwine.parameter.Parameter;

public class TestParameter {

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {

  }

  @Test
  public void testParameterIsRequiredByDefault() {
    Parameter p = Parameter.create("temp");
    assertTrue(p.isRequired());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParameterTypeThrowsErrorOnInvalidType() {
    Parameter.parseTypeString("intsect");
  }

  @Test
  public void testParameterTypeCanParseInteger() {
    assertEquals(Integer.class, Parameter.parseTypeString("int"));
    assertEquals(Integer.class, Parameter.parseTypeString("integer"));
    assertEquals(Integer.class, Parameter.parseTypeString("Integer"));
  }

  @Test
  public void testParameterTypeCanParseDate() {
    assertEquals(Date.class, Parameter.parseTypeString("date"));
    assertEquals(Date.class, Parameter.parseTypeString("Date"));
  }

  @Test
  public void testParameterTypeCanParseFloat() {
    assertEquals(Float.class, Parameter.parseTypeString("float"));
    assertEquals(Float.class, Parameter.parseTypeString("Float"));
  }

  @Test
  public void testParameterTypeCanParseString() {
    assertEquals(String.class, Parameter.parseTypeString("string"));
    assertEquals(String.class, Parameter.parseTypeString("String"));
  }

  @Test
  public void testParameterTypeCanParseBoolean() {
    assertEquals(Boolean.class, Parameter.parseTypeString("bool"));
    assertEquals(Boolean.class, Parameter.parseTypeString("boolean"));
    assertEquals(Boolean.class, Parameter.parseTypeString("Boolean"));
  }

  @Test
  public void testParameterTypeCanParseResultSet() {
    assertEquals(DataResultSet.class, Parameter.parseTypeString("resultset"));
    assertEquals(DataResultSet.class, Parameter.parseTypeString("DataResultSet"));
    assertEquals(DataResultSet.class, Parameter.parseTypeString("ResultSet"));
  }
}
