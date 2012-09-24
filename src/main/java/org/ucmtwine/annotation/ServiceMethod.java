/**
 * 
 */
package org.ucmtwine.annotation;

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
   * Read access level: User requires read (R) permissions on the security group
   * referenced in the service.
   */
  int ACCESS_READ = 1;

  /**
   * Write access level: User requires write (W) permissions on the security
   * group referenced in the service.
   */
  int ACCESS_WRITE = 2;

  /**
   * Delete access level: User requires delete (D) permissions on the security
   * group referenced in the service.
   */
  int ACCESS_DELETE = 4;

  /**
   * Admin access level: User requires admin (A) permissions on the security
   * group referenced in the service.
   */
  int ACCESS_ADMIN = 8;

  /**
   * Global access level: Used if the service does not act on documents. When
   * global is used, it also requires at least one other RWDA permission, this
   * checks the user has the specified permission on ANY security group.
   */
  int ACCESS_GLOBAL = 16;

  /**
   * Scriptable access level: The service can be called inside idoc scripts.
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
   * The access level of the service. Defaults to Global, Scriptable, Read
   */
  int accessLevel() default ACCESS_READ | ACCESS_GLOBAL | ACCESS_SCRIPTABLE;

  /**
   * The notified subjects.
   */
  String subjects() default "";
}
