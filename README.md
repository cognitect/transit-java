# transit-java

Transit is a data format and a set of libraries for transferring values between applications written in different languages. This library provides support for marshalling Transit data to/from Java.

* [Rationale](http://i-should-be-a-link)
* [API docs](http://cognitect.github.io/transit-java/)
* [Specification](http://github.com/cognitect/transit-format)

## Releases and Dependency Information

* Latest release: TBD
* [All Released Versions](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cognitect%22%20AND%20a%3A%22transit-java%22)

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>com.cognitect</groupId>
  <artifactId>transit-java</artifactId>
  <version>TBD</version>
</dependency>
```

## Usage

```java
import cognitect.transit.TransitFactory;

public static Object roundtrip(Object data) {
    // Write the data to a stream
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Writer writer = TransitFactory.writer(TransitFactory.Format.MSGPACK, baos);
    writer.write(data);

    // Read the data from a stream
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Reader reader = TransitFactory.reader(TransitFactory.Format.MSGPACK, bais);
    return reader.read();
}

public static void testRoundtrip() {
    // Create some data
    List<String> list1 = new ArrayList<String>();
    Collections.addAll(list1, "red", "green", "blue");
    List<String> list2 = new ArrayList<String>();
    Collections.addAll(list2, "apple", "pear", "grape");
    Map<Long,List<String>> data = new HashMap<Long,List<String>>();
    data.put(1L, list1);
    data.put(2L, list2);

    // Verify data is the same
    Map<Long,List<String>> transmitted = (Map<Long,List<String>>) roundtrip(data);
    assert(data.equals(transmitted));
}
```

## Type Mapping

TBD

## Contributing 

Please discuss potential problems or enhancements on the [transit-format mailing list](https://groups.google.com/forum/#!forum/transit-format). Issues should be filed using GitHub issues for this project.

Contributing to Cognitect projects requires a signed [Cognitect Contributor Agreement](http://cognitect.com/contributing).


## Copyright and License

Copyright © 2014 Cognitect

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
