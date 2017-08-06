/**
 *
 */
package com.github.ginvavilon.ajson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vladimir Baraznovsky
 *
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.CLASS)
public @interface JsonGetField {
    String value();

    boolean ignoreNull() default false;
}
