package org.stirrat.twine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Filter {
	int loadOrder() default 100;
	String event(); // only filters that run after extraAfterConfigInit
	String parameter() default "";
}
