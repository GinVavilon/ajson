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

@Target(value=ElementType.METHOD)
@Retention(value= RetentionPolicy.CLASS)
public @interface JsonSetField{
    String value();
    JsonType type() default JsonType.AUTO ;

    int order() default 0;
    boolean ignoreNull() default false;
}

