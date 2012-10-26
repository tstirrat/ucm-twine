package org.ucmtwine.annotation;

public @interface Template {
  /**
   * The template identifier
   * 
   * @return
   */
  public String name();

  /**
   * The template's description
   * 
   * @return
   */
  public String description() default "";

  /**
   * The location on the classpath of the template source file
   * 
   * @return
   */
  public String src();
}
