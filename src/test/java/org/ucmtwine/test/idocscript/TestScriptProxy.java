package org.ucmtwine.test.idocscript;

import static org.junit.Assert.assertArrayEquals;
import intradoc.common.GrammarElement;
import intradoc.common.ScriptExtensions;

import org.junit.Before;
import org.junit.Test;
import org.ucmtwine.parameter.Parameter;
import org.ucmtwine.proxy.ScriptProxy;

public class TestScriptProxy {
  ScriptExtensions scriptPackage;

  @Before
  public void setUp() {
    scriptPackage = new ScriptProxy(TestScriptPackage.class);
  }

  @Test
  public void testGenerateFunctionDefinitions_ShouldReturnFunctionsAlphabetically() {
    assertArrayEquals(new String[] { "factorial", "log", "strMin" }, scriptPackage.getFunctionTable());
  }

  @Test
  public void testGenerateFunctionDefinition_ShouldReturnCorrectParameterAndReturnTypes() {
    assertArrayEquals(new int[][] {
        { 0, 1, GrammarElement.INTEGER_VAL, Parameter.GRAMMAR_ELEMENT_UNSPECIFIED, ScriptProxy.RETURN_INTEGER }, // factorial(int)
        { 1, 1, GrammarElement.STRING_VAL, Parameter.GRAMMAR_ELEMENT_UNSPECIFIED, ScriptProxy.RETURN_VOID }, // log(String)
        { 2, 2, GrammarElement.STRING_VAL, GrammarElement.STRING_VAL, ScriptProxy.RETURN_STRING }, // strMin(String,String)
    }, scriptPackage.getFunctionDefinitionTable());
  }

  @Test
  public void testGenerateVariableDefinitions_ShouldReturnVariablesAlphabetically() {
    assertArrayEquals(new String[] { "TodaysDateIsEven", "UppercaseUserName" }, scriptPackage.getVariableTable());
  }

  @Test
  public void testGenerateVariableDefinitions_ShouldYieldCorrectReturnTypes() {
    assertArrayEquals(new int[][] { //
        { 0, ScriptProxy.RETURN_BOOLEAN }, // TodaysDateIsEven(boolean)
            { 1, ScriptProxy.RETURN_STRING } // UppercaseUserName(String)
        }, scriptPackage.getVariableDefinitionTable());

  }
}
