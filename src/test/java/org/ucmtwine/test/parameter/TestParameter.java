package org.ucmtwine.test.parameter;

import static org.junit.Assert.assertTrue;

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
}
