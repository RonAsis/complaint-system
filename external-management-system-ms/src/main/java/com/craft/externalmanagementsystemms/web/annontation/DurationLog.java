package com.craft.externalmanagementsystemms.web.annontation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * do log of in and out for method.
 * write the the time in the start of the method and in the end
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DurationLog {
}
