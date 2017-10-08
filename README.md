Annotation processor for parsing Json with using android parser.

## Configure gradle
Add dependencies

```Gradle
dependencies {

    compile 'com.github.ginvavilon.ajson:ajson-annotations:0.9.0'
    compile 'com.github.ginvavilon.ajson:ajson-objects:0.9.0'
    annotationProcessor 'com.github.ginvavilon.ajson:ajson-processor:0.9.0'

}
```

## Simple configuration

Configure interface

```Java
@JsonObject("NameInterfaceImpl") // NameInterfaceImpl is class for creating implementation of interface
public interface NameInterface {

    // Configure getter
    @JsonGetField("name") 
    String getName();

    // Configure setter
    @JsonSetField("name")
    void setName(String setName);

    // Configure setter
    @JsonField("value")
    void setValue(String value);
    
    // Configure getter
    @JsonField("value")
    String getValue();
}
```

Configure object

```Java
@JsonObject
public class NameObject {
    
    // Configure field. If field is private setter (setName) and getter (getName) will be used
    @JsonField("field")
    NameInterfaceImpl mFiled;
    
    // Method for creating instance of fileds
    @JsonFieldCreator("field")
    TestInterface createHideValue() {
        return new NameInterfaceImpl();
    }
    
    // Method returns parsing of fields
    @JsonFieldParser("field")
    TestInterfaceJson getFieldParser() {
        return TestInterfaceJson.getInstance();
    }
    
    // Method returns converter of fields to JSON
    @JsonFieldConverter("field")
    TestInterfaceJson getFieldConverter() {
        return TestInterfaceJson.getInstance();
    }

}
```
## Simple usage

Serialize

```Java
JSONObject jsonObject = NameInterfaceJson.getInstance().toJson(object);
JSONArray jsonArray   = NameInterfaceJson.getInstance().toJson(collectionOfObjects);
JSONArray jsonArray   = NameInterfaceJson.getInstance().toJson(arrayOfObjects);
```
Deserialize

```Java
NameInterface object = NameInterfaceJson.getInstance().parse(jsonObject);

NameInterface existObject = new NameInterfaceImpl();
NameInterfaceJson.getInstance().parse(jsonObject,existObject);

List<NameInterface> listObjects = NameInterfaceJson.getInstance().parseList(jsonArray);
Map<String,NameInterface> mapObjects = NameInterfaceJson.getInstance().parseMap(jsonObject)

```

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
