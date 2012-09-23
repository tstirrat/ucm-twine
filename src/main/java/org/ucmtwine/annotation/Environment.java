package org.ucmtwine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an injectable ucm environment variable.
 * 
 * @author tim
 */
@Target(ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Environment {
  /**
   * The name of the environment variable.
   */
  String name();

  /**
   * Indicates the environment variable is required, causing an exception if not
   * specified.
   */
  boolean required() default true;
}
