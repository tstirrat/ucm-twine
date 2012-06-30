package org.stirrat.twine.test.idocscript;

import static org.junit.Assert.assertArrayEquals;
import intradoc.common.GrammarElement;
import intradoc.common.ScriptExtensions;

import org.junit.Test;
import org.stirrat.twine.parameter.Parameter;
import org.stirrat.twine.proxy.ScriptProxy;

public class TestScriptProxy {

  // @Test
  public void testGenerateFunctionDefinitions() {
    ScriptExtensions scriptPackage = new ScriptProxy(TestScriptPackage.class);

    assertArrayEquals(new int[][] {
        { 0, 1, GrammarElement.INTEGER_VAL, Parameter.GRAMMAR_ELEMENT_UNSPECIFIED, ScriptProxy.RETURN_INTEGER }, // factorial(int)
        { 1, 2, GrammarElement.STRING_VAL, GrammarElement.STRING_VAL, ScriptProxy.RETURN_STRING }, // strMin(String,String)
        { 2, 1, GrammarElement.STRING_VAL, Parameter.GRAMMAR_ELEMENT_UNSPECIFIED, ScriptProxy.RETURN_VOID }, // log(String)
    }, scriptPackage.getFunctionDefinitionTable());

    assertArrayEquals(new String[] { "factorial", "strMin", "log" }, scriptPackage.getFunctionTable());
  }

  @Test
  public void testGenerateVariableDefinitions() {
    ScriptExtensions scriptPackage = new ScriptProxy(TestScriptPackage.class);

    assertArrayEquals(new int[][] { { 0, ScriptProxy.RETURN_STRING }, // UppercaseUserName
                                                                      // (String)
        { 1, ScriptProxy.RETURN_BOOLEAN }, // TodaysDateIsEven (boolean)
    }, scriptPackage.getVariableDefinitionTable());

    assertArrayEquals(new String[] { "UppercaseUserName", "TodaysDateIsEven" }, scriptPackage.getVariableTable());
  }
}
