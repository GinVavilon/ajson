/**
 * 
 */
package com.github.ginvavilon.ajson.example;

import com.github.ginvavilon.ajson.annotations.JsonGetField;
import com.github.ginvavilon.ajson.annotations.JsonObject;
import com.github.ginvavilon.ajson.annotations.JsonSetField;

/**
 * @author vbaraznovsky
 *
 */
@JsonObject
public interface TestInterface {
    @JsonGetField("name")
    String getName();

    @JsonSetField("name")
    void setName(String setName);
}
