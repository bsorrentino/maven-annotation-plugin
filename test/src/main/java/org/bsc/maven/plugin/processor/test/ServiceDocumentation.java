package org.bsc.maven.plugin.processor.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD})
public @interface ServiceDocumentation {

	public String since() default "";
	
	public String value() default "";
	
	public String note() default "";
}
