/**
 * 
 */
package org.stirrat.twine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a UCM service entry point.
 * 
 * @author tim
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServiceMethod {

  /**
   * Read access level: User required read (R) access permissions (?).
   */
  int ACCESS_READ = 1;

  /**
   * Write access level: User requires write (W) access permissions (?).
   */
  int ACCESS_WRITE = 2;

  /**
   * Delete access level: User requires delete (D) access permissions (?).
   */
  int ACCESS_DELETE = 4;

  /**
   * Admin access level: User requires admin (A) access permissions (?).
   */
  int ACCESS_ADMIN = 8;

  /**
   * Global access level.
   */
  int ACCESS_GLOBAL = 16;

  /**
   * Scriptable access level: can be called inside scripts e.g. IDOC.
   */
  int ACCESS_SCRIPTABLE = 32;

  /**
   * The UCM service name.
   */
  String name();

  /**
   * The UCM service template.
   */
  String template() default "";

  /**
   * An error message to return if the service fails to execute.
   */
  String errorMessage() default "Error executing service";

  /**
   * The type of service. e.g. Normal (null) or SubService.
   */
  String type() default "";

  /**
   * The access level of the service.
   */
  int accessLevel() default ACCESS_GLOBAL | ACCESS_SCRIPTABLE;

  /**
   * The notified subjects.
   */
  String subjects() default "";
}
