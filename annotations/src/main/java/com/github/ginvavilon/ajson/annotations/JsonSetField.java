/**
 *
 */
package com.github.ginvavilon.ajson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark method as setter of field
 * 
 * @author Vladimir Baraznovsky
 *
 */

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.CLASS)
public @interface JsonSetField {
    String value();

    JsonType type() default JsonType.AUTO;

    boolean ignoreNull() default false;
}
