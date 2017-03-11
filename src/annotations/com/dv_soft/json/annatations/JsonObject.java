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
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.CLASS)
public @interface JsonObject {
    String instance() default "";

}