package org.ucmtwine.test.idocscript;

import static org.junit.Assert.assertArrayEquals;
import intradoc.common.GrammarElement;
import intradoc.common.ScriptExtensions;

import org.junit.Before;
import org.junit.Test;
import org.ucmtwine.parameter.Parameter;
import org.ucmtwine.proxy.ScriptProxy;
import org.ucmtwine.test.idocscript.classes.HowToComponentsPackage;

public class TestScriptProxyInitialisation {
  ScriptExtensions scriptPackage;

  @Before
  public void setUp() {
    scriptPackage = new ScriptProxy(HowToComponentsPackage.class);
  }

  @Test
  public void testGenerateFunctionDefinitions_ShouldReturnFunctionsAlphabetically() {
    assertArrayEquals(new String[] { "factorial", "log", "renamedFunc", "strMin" }, scriptPackage.getFunctionTable());
  }

  @Test
  public void testGenerateFunctionDefinition_ShouldReturnCorrectParameterAndReturnTypes() {
    assertArrayEquals(new int[][] {
        { 0, 1, GrammarElement.INTEGER_VAL, Parameter.GRAMMAR_ELEMENT_UNSPECIFIED, ScriptProxy.RETURN_INTEGER }, // factorial(int)
        { 1, 1, GrammarElement.STRING_VAL, Parameter.GRAMMAR_ELEMENT_UNSPECIFIED, ScriptProxy.RETURN_VOID }, // log(String)
        { 2, 1, GrammarElement.STRING_VAL, Parameter.GRAMMAR_ELEMENT_UNSPECIFIED, ScriptProxy.RETURN_INTEGER }, // renamedFunc(String)
        { 3, 2, GrammarElement.STRING_VAL, GrammarElement.STRING_VAL, ScriptProxy.RETURN_STRING }, // strMin(String,String)
    }, scriptPackage.getFunctionDefinitionTable());
  }

  @Test
  public void testGenerateVariableDefinitions_ShouldReturnVariablesAlphabetically() {
    assertArrayEquals(new String[] { "never", "TodaysDateIsEven", "UppercaseUserName" },
        scriptPackage.getVariableTable());
  }

  @Test
  public void testGenerateVariableDefinitions_ShouldYieldCorrectReturnTypes() {
    assertArrayEquals(new int[][] { //
        { 0, ScriptProxy.RETURN_BOOLEAN }, // never(boolean)
            { 1, ScriptProxy.RETURN_BOOLEAN }, // TodaysDateIsEven(boolean)
            { 2, ScriptProxy.RETURN_STRING } // UppercaseUserName(String)
        }, scriptPackage.getVariableDefinitionTable());

  }
}
