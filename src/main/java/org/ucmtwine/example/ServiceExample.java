package org.ucmtwine.example;

import intradoc.server.HttpImplementor;

import org.ucmtwine.annotation.Binder;
import org.ucmtwine.annotation.ServiceMethod;

public class ServiceExample {

  @ServiceMethod(name = "TEST_SERVICE", accessLevel = ServiceMethod.ACCESS_READ | ServiceMethod.ACCESS_GLOBAL)
  public void endpoint(@Binder(name = "code") String code, HttpImplementor http, int a) {
    http.setServerTooBusy(true);
  }
}
