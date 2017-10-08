/**
 *
 */
package com.github.ginvavilon.ajson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark field or method(getter or setter) as field of JSON
 * 
 * @author Vladimir Baraznovsky
 *
 */
@Target(value = { ElementType.FIELD, ElementType.METHOD })
@Retention(value = RetentionPolicy.CLASS)
public @interface JsonField {
    String value();

    JsonType type() default JsonType.AUTO;

    boolean ignoreNull() default false;
}
