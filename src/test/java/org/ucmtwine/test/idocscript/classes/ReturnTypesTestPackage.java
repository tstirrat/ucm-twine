package org.ucmtwine.test.idocscript.classes;

import java.util.Date;

import org.ucmtwine.annotation.IdocFunction;

public class ReturnTypesTestPackage {
  @IdocFunction
  public void returnVoid() {

  }

  @IdocFunction
  public String returnNull() {
    return null;
  }

  @IdocFunction
  public boolean returnBooleanPrimitive() {
    return true;
  }

  @IdocFunction
  public Boolean returnBoolean() {
    return true;
  }

  @IdocFunction
  public int returnIntegerPrimitive() {
    return 20;
  }

  @IdocFunction
  public Integer returnInteger() {
    return 20;
  }

  @IdocFunction
  public long returnLongPrimitive() {
    return 20L;
  }

  @IdocFunction
  public Long returnLong() {
    return 20L;
  }

  @IdocFunction
  public float returnFloatPrimitive() {
    return 0.1f;
  }

  @IdocFunction
  public Float returnFloat() {
    return 0.1f;
  }

  @IdocFunction
  public double returnDoublePrimitive() {
    return 0.1d;
  }

  @IdocFunction
  public Double returnDouble() {
    return 0.1d;
  }

  @IdocFunction
  public Date returnDate() {
    return new Date();
  }

}
