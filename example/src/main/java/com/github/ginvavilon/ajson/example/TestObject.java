/**
 * 
 */
package com.github.ginvavilon.ajson.example;

import com.github.ginvavilon.ajson.annotations.JsonField;
import com.github.ginvavilon.ajson.annotations.JsonFieldCreator;
import com.github.ginvavilon.ajson.annotations.JsonObject;

@JsonObject
public class TestObject {

    @JsonField("name")
    String mName;

    @JsonField("value")
    InnerTest mValue;

    @JsonField("hideValue")
    TestInterface mHideValue;

    @JsonFieldCreator("hideValue")
    TestInterface createHideValue() {
        return new TestInterface() {

            private String mHideName;

            @Override
            public void setName(String name) {
                mHideName = name;

            }

            @Override
            public String getName() {
                return mHideName;
            }
        };

    }

    @JsonObject
    static class InnerTest {
        @JsonField("value")
        String mValue;
    }
}
