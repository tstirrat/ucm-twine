package org.stirrat.twine.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import intradoc.common.ExecutionContext;
import intradoc.common.GrammarElement;
import intradoc.common.ServiceException;
import intradoc.data.DataBinder;
import intradoc.data.DataException;
import intradoc.data.DataResultSet;
import intradoc.server.Service;

import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.stirrat.twine.annotation.Binder;
import org.stirrat.twine.parameter.IParameter;
import org.stirrat.twine.parameter.ParameterMarshaller;
import org.stirrat.twine.proxy.ScriptProxy;

@RunWith(MockitoJUnitRunner.class)
public class TestParameterMarshaller {

  ParameterMarshaller pbEmpty;
  ParameterMarshaller pbUsingMethodNoParams;
  ParameterMarshaller pbUsingMethodWithParams;
  ParameterMarshaller pbUsingParamList;

  Class<?> methodParams[][] = new Class<?>[][] { {}, // methodWithNoParameters
      { String.class, int.class, boolean.class, Boolean.class, DataResultSet.class }, // methodWithParameters
      { boolean.class }, // methodWithIncorrectNonNullableParameter
      { Integer.class, ExecutionContext.class, DataBinder.class }, // methodWithInjectableTypesAtEnd
      { Integer.class, ExecutionContext.class, Float.class }, // methodWithInjectableTypesInMiddle
      { int.class, boolean.class, int.class } // methodWithAnonymousType
  };

  DataBinder testBinder;
  
  @Mock
  private Service testService;

  // Vector<String> paramVector;

  public void methodWithNoParameters() {
  }

  public void methodWithParameters(@Binder(name = "a") String a, @Binder(name = "b") int b,
      @Binder(name = "c") boolean c, @Binder(name = "d", required = false) Boolean d,
      @Binder(name = "testRs") DataResultSet testRs) {
  }

  public void methodWithIncorrectNonNullableParameter(@Binder(name = "a", required = false) boolean a) {
  }

  public void methodWithInjectableTypesAtEnd(@Binder(name = "a") Integer a, ExecutionContext ctx, DataBinder binder) {
  }

  public void methodWithInjectableTypesInMiddle(@Binder(name = "a") Integer a, ExecutionContext ctx,
      @Binder(name = "b") Float b) {
  }

  public void methodWithAnonymousType(@Binder(name = "a") int a, boolean b, @Binder(name = "c") int c) {
  }

  @Before
  public void setUp() throws SecurityException, DataException, NoSuchMethodException, ClassNotFoundException,
      ServiceException {
    pbEmpty = new ParameterMarshaller();

    pbUsingMethodNoParams = new ParameterMarshaller(this.getClass()
        .getMethod("methodWithNoParameters", methodParams[0]));

    pbUsingMethodWithParams = new ParameterMarshaller(this.getClass()
        .getMethod("methodWithParameters", methodParams[1]));

    testBinder = new DataBinder();
    testBinder.putLocal("a", "paramA");
    testBinder.putLocal("b", "1");
    testBinder.putLocal("c", "true");
    testBinder.putLocal("d", "false");
    testBinder.addResultSet("testRs", new DataResultSet());

    Vector<String> paramVector = new Vector<String>();

    paramVector.add("java.lang.String");
    paramVector.add("a");
    paramVector.add("required");

    paramVector.add("int");
    paramVector.add("b");
    paramVector.add("required");

    paramVector.add("boolean");
    paramVector.add("c");
    paramVector.add("required");

    paramVector.add("java.lang.Boolean");
    paramVector.add("d");
    paramVector.add("");

    paramVector.add("intradoc.data.DataResultSet");
    paramVector.add("testRs");
    paramVector.add("required");

    pbUsingParamList = new ParameterMarshaller(paramVector);

    when(testService.getBinder()).thenReturn(testBinder);
  }

  @Test
  public void testDefaultStateIsEmpty() {
    assertEquals(0, pbEmpty.getTypeArray().length);
    assertEquals(0, pbEmpty.getValueArray(testService).length);
  }

  @Test(expected = NoSuchMethodException.class)
  public void testParsingInvalidMethodThrowsException() throws NoSuchMethodException, SecurityException, DataException,
      IllegalArgumentException, ClassNotFoundException {
    @SuppressWarnings("unused")
    ParameterMarshaller pb = new ParameterMarshaller(this.getClass()
        .getMethod("methodThatDoesntExist", methodParams[0]));
  }

  @Test
  public void testParsingMethodWithNoParametersEnumeratesCorrectParameterTypes() throws SecurityException,
      DataException, NoSuchMethodException {

    assertEquals(0, pbUsingMethodNoParams.getTypeArray().length);
    assertEquals(0, pbUsingMethodNoParams.getValueArray(testService).length);
  }

  @Test
  public void testParsingMethodWithNoParametersEnumeratesCorrectParameters() throws SecurityException, DataException,
      NoSuchMethodException {

    List<IParameter> binderVars = pbUsingMethodNoParams.getParameters();

    assertEquals(0, binderVars.size());
  }

  @Test
  public void testParsingMethodWithParametersEnumeratesCorrectParameterTypes() throws SecurityException, DataException,
      NoSuchMethodException {

    Class<?> types[] = pbUsingMethodWithParams.getTypeArray();
    Object params[] = pbUsingMethodWithParams.getValueArray(testService);
    List<IParameter> binderVars = pbUsingMethodWithParams.getParameters();

    assertEquals(5, types.length);
    assertEquals(5, params.length);
    assertEquals(5, binderVars.size());

    // class types
    assertEquals(String.class, types[0]);
    assertEquals(int.class, types[1]);
    assertEquals(boolean.class, types[2]);
    assertEquals(Boolean.class, types[3]);
    assertEquals(DataResultSet.class, types[4]);
  }

  @Test
  public void testParsingParameterStringEnumeratesCorrectParameterTypes() {

    Class<?> types[] = pbUsingParamList.getTypeArray();
    Object params[] = pbUsingParamList.getValueArray(testService);
    List<IParameter> binderVars = pbUsingParamList.getParameters();

    assertEquals(5, types.length);
    assertEquals(5, params.length);
    assertEquals(5, binderVars.size());

    // class types
    assertEquals(String.class, types[0]);
    assertEquals(int.class, types[1]);
    assertEquals(boolean.class, types[2]);
    assertEquals(Boolean.class, types[3]);
    assertEquals(DataResultSet.class, types[4]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOptionalParameterWithNonNullableTypeThrowsAnException() throws SecurityException, DataException,
      NoSuchMethodException, IllegalArgumentException, ClassNotFoundException {
    @SuppressWarnings("unused")
    ParameterMarshaller m = new ParameterMarshaller(this.getClass().getMethod(
        "methodWithIncorrectNonNullableParameter", methodParams[2]));
  }

  @Test
  public void testFunctionDefinitionTableReturnsCorrectly() throws IllegalArgumentException, SecurityException,
      NoSuchMethodException {
    assertArrayEquals(
        new int[] { 0, 5, GrammarElement.STRING_VAL, GrammarElement.INTEGER_VAL, GrammarElement.INTEGER_VAL,
            GrammarElement.INTEGER_VAL, GrammarElement.STRING_VAL, -1, ScriptProxy.RETURN_VOID },
        pbUsingMethodWithParams.getFunctionDefinitionArray(0, 6, ScriptProxy.RETURN_VOID));

    ParameterMarshaller m = new ParameterMarshaller(this.getClass().getMethod("methodWithInjectableTypesInMiddle",
        methodParams[4]));

    assertArrayEquals(new int[] { 0, 2, GrammarElement.INTEGER_VAL, GrammarElement.FLOAT_VAL, -1, -1, -1, -1,
        ScriptProxy.RETURN_VOID }, m.getFunctionDefinitionArray(0, 6, ScriptProxy.RETURN_VOID));

    ParameterMarshaller m2 = new ParameterMarshaller(this.getClass().getMethod("methodWithInjectableTypesAtEnd",
        methodParams[3]));

    assertArrayEquals(new int[] { 0, 1, GrammarElement.INTEGER_VAL, -1, -1, -1, -1, ScriptProxy.RETURN_STRING },
        m2.getFunctionDefinitionArray(0, 5, ScriptProxy.RETURN_STRING));
  }

  @Test
  public void testFunctionDefinitionTablePadsParametersCorrectly() throws IllegalArgumentException, SecurityException,
      NoSuchMethodException {
    assertArrayEquals(new int[] { 0, 5, GrammarElement.STRING_VAL, GrammarElement.INTEGER_VAL,
        GrammarElement.INTEGER_VAL, GrammarElement.INTEGER_VAL, GrammarElement.STRING_VAL, ScriptProxy.RETURN_VOID },
        pbUsingMethodWithParams.getFunctionDefinitionArray(0, 5, ScriptProxy.RETURN_VOID));

    assertArrayEquals(
        new int[] { 0, 5, GrammarElement.STRING_VAL, GrammarElement.INTEGER_VAL, GrammarElement.INTEGER_VAL,
            GrammarElement.INTEGER_VAL, GrammarElement.STRING_VAL, -1, ScriptProxy.RETURN_VOID },
        pbUsingMethodWithParams.getFunctionDefinitionArray(0, 6, ScriptProxy.RETURN_VOID));

    assertArrayEquals(new int[] { 0, 5, GrammarElement.STRING_VAL, GrammarElement.INTEGER_VAL,
        GrammarElement.INTEGER_VAL, GrammarElement.INTEGER_VAL, GrammarElement.STRING_VAL, -1, -1, -1, -1, -1,
        ScriptProxy.RETURN_VOID }, pbUsingMethodWithParams.getFunctionDefinitionArray(0, 10, ScriptProxy.RETURN_VOID));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetFunctionDefinitionTableThrowsExceptionWhenSupplyingTooLittleParameterCount() {
    @SuppressWarnings("unused")
    int[] temp = pbUsingMethodWithParams.getFunctionDefinitionArray(0, 3, ScriptProxy.RETURN_VOID);
  }

  @Test
  public void testUnnamedParameterSupport() {

  }

  @Test
  public void testUnnamedParametersReturnNullFromBinder() {

  }

  @Test
  public void testUnnamedParametersReturnCorrectlyFromArguments() {

  }

  @Test
  public void testParameterValuesAreCorrectWithInjectionAtEnd() {

  }

  @Test
  public void testParameterValuesAreCorrectWithInjectionBetweenParameters() {

  }

  @Test
  public void testUserDataParameterIsInjectedCorrectly() {

  }

  @Test
  public void testBinderParameterIsInjectedCorrectly() {

  }

  @Test
  public void testSeviceParameterIsInjectedCorrectly() {

  }

  @Test
  public void testServiceParameterInjectionWhenNotInsideValidServiceContextThrowsException() {

  }

  @Test
  public void testExecutionContextParameterIsInjectedCorrectly() {

  }

  public void testReturnTypeBooleanConvertsToLong() {

  }

  public void testReturnTypeIntegerConvertsToLong() {

  }

  public void testReturnTypeString() {

  }

  public void testReturnTypeDate() {

  }
}
