package org.stirrat.twine.example;

import intradoc.server.HttpImplementor;

import org.stirrat.twine.annotation.Binder;
import org.stirrat.twine.annotation.ServiceMethod;

public class ServiceExample {

  @ServiceMethod(name = "TEST_SERVICE")
  public void endpoint(@Binder(name = "code") String code, HttpImplementor http, int a) {
    http.setServerTooBusy(true);
  }
}
