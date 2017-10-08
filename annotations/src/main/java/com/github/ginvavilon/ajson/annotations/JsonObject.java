/**
 *
 */
package com.github.ginvavilon.ajson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark object or interface for serialize as JSON
 * 
 * @author Vladimir Baraznovsky
 *
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.CLASS)
public @interface JsonObject {

    /**
     * Name class for initialize with default constructor
     */
    String instance() default "";

}
