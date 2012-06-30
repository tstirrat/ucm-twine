/**
 * 
 */
package org.ucmtwine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a ucm binder variable for use in services and filters.
 * 
 * @author tim
 */
@Target(ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Binder {
	/**
	 * The name of the variable in the binder.
	 */
	String name();

	/**
	 * Indicates the parameter is required, causing an exception if not
	 * supplied.
	 */
	boolean required() default true;
}