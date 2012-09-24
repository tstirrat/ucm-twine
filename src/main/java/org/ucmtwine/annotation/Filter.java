package org.ucmtwine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a UCM filter
 * 
 * @author tim
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Filter {

  /**
   * The class/filter load order.
   */
  int loadOrder() default 100;

  /**
   * The filter event to execute on.
   * 
   * <p>
   * <b>NOTE:</b> Because the Twine bootstrapper executes on the
   * extraAfterConfigInit filter event, only filters that run after the
   * extraAfterConfigInit event will work here.
   * </p>
   */
  String event();

  /**
   * The filter's parameter.
   */
  String parameter() default "";
}
