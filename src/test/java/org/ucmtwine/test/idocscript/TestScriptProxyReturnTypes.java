package org.ucmtwine.test.idocscript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import intradoc.common.ExecutionContext;
import intradoc.common.ScriptInfo;
import intradoc.server.Service;
import intradoc.shared.UserData;

import org.junit.Before;
import org.junit.Test;
import org.ucmtwine.proxy.ScriptProxy;
import org.ucmtwine.test.idocscript.classes.ReturnTypesTestPackage;

public class TestScriptProxyReturnTypes {
  private static final double DOUBLE_COMPARISON_DELTA = 1e-10;
  private ScriptProxy proxy;
  private ExecutionContext ctx;

  @SuppressWarnings("deprecation")
  @Before
  public void setUp() {
    proxy = new ScriptProxy(ReturnTypesTestPackage.class);
    ctx = new Service();
    ctx.setCachedObject("UserData", new UserData());
  }

  @Test
  public void testVoidReturnType_ShouldYieldANullResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnVoid");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertNull(args[0]);
  }

  @Test
  public void testReturningNullValue_ShouldYieldANullResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnNull");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertNull(args[0]);
  }

  @Test
  public void testLongReturnType_ShouldYieldALongResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnLong");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Long.class, args[0].getClass());
    assertEquals(new Long(20L), (Long) args[0]);
  }

  @Test
  public void testLongPrimitiveReturnType_ShouldYieldALongResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnLongPrimitive");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Long.class, args[0].getClass());
    assertEquals(new Long(20L), (Long) args[0]);
  }

  @Test
  public void testBooleanReturnType_ShouldYieldALongResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnBoolean");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Long.class, args[0].getClass());
    assertEquals(new Long(1L), (Long) args[0]);
  }

  @Test
  public void testBooleanPrimitiveReturnType_ShouldYieldALongResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnBooleanPrimitive");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Long.class, args[0].getClass());
    assertEquals(new Long(1L), (Long) args[0]);
  }

  @Test
  public void testIntegerPrimitiveReturnType_ShouldYieldALongResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnIntegerPrimitive");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Long.class, args[0].getClass());
    assertEquals(new Long(20L), (Long) args[0]);
  }

  @Test
  public void testIntegerReturnType_ShouldYieldALongResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnInteger");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Long.class, args[0].getClass());
    assertEquals(new Long(20L), (Long) args[0]);
  }

  @Test
  public void testFloatPrimitiveReturnType_ShouldYieldADoubleResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnFloatPrimitive");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Double.class, args[0].getClass());
    assertEquals(new Double(0.1d).floatValue(), ((Double) args[0]).floatValue(), DOUBLE_COMPARISON_DELTA);
  }

  @Test
  public void testFloatReturnType_ShouldYieldADoubleResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnFloat");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Double.class, args[0].getClass());
    assertEquals(new Double(0.1d).floatValue(), ((Double) args[0]).floatValue(), DOUBLE_COMPARISON_DELTA);
  }

  @Test
  public void testDoublePrimitiveReturnType_ShouldYieldADoubleResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnDoublePrimitive");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Double.class, args[0].getClass());
    assertEquals(new Double(0.1d), ((Double) args[0]), DOUBLE_COMPARISON_DELTA);
  }

  @Test
  public void testDoubleReturnType_ShouldYieldADoubleResult() throws Exception {
    ScriptInfo info = getScriptInfo("returnDouble");

    Object args[] = new Object[1];
    proxy.evaluateFunction(info, args, ctx);

    assertEquals(Double.class, args[0].getClass());
    assertEquals(new Double(0.1d), ((Double) args[0]), DOUBLE_COMPARISON_DELTA);
  }

  private ScriptInfo getScriptInfo(String functionName) throws Exception {
    ScriptInfo info = new ScriptInfo();

    info.m_key = functionName;
    info.m_entry = getFunctionEntry(functionName);
    return info;
  }

  private int[] getFunctionEntry(String functionName) throws Exception {
    String functionTable[] = proxy.getFunctionTable();
    for (int i = 0; i < functionTable.length; i++) {
      if (functionTable[i].equalsIgnoreCase(functionName)) {
        return proxy.getFunctionDefinitionTable()[i];
      }
    }
    throw new Exception("Unknown function");
  }
}
