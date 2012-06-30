package org.ucmtwine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IdocFunction {

  /**
   * Function name, if not the same as the method name.
   */
  String name() default "";
  
	/**
	 * Can the output of the function be cached?
	 * @return
	 */
	boolean cacheable() default false;
}
