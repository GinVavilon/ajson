/**
 *
 */
package com.dv_soft.json.annatations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vladimir Baraznovsky
 *
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.CLASS)
public @interface JsonField {
    String value();

    JsonType type() default JsonType.AUTO;

    boolean ignoreNull() default false;
}
