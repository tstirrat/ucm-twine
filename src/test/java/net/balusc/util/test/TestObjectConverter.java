package net.balusc.util.test;

import static org.junit.Assert.*;

import net.balusc.util.ObjectConverter;

import org.junit.Test;

public class TestObjectConverter {

  @Test
  public void testLongToPrimitiveConversion() {
    long test = ObjectConverter.convert(new Long(3L), long.class);
    
    assertEquals(3L, new Long(3L).longValue());
    assertEquals(3L, test);
  }
  
  @Test
  public void testStringToPrimitiveLong() {
    long test = ObjectConverter.convert("3", long.class);
    
    
    assertEquals(3L, test);
  }

}
