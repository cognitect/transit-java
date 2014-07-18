# transit-java

Transit is a data format and a set of libraries for conveying values between applications written in different languages. This library provides support for marshalling Transit data to/from Java.

* [Rationale](http://blog.cognitect.com/blog/2014/7/22/transit)
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
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import com.cognitect.transit.TransitFactory;
import com.cognitect.transit.Reader;
import com.cognitect.transit.Writer;

// Write the data to a stream
ByteArrayOutputStream baos = new ByteArrayOutputStream();
Writer writer = TransitFactory.writer(TransitFactory.Format.MSGPACK, baos);
writer.write(data);

// Read the data from a stream
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
Reader reader = TransitFactory.reader(TransitFactory.Format.MSGPACK, bais);
Object data = reader.read();
```

## Default Type Mapping

|Transit type|Write accepts|Read returns|
|------------|-------------|------------|
|null|null|null|
|string|java.lang.String|java.lang.String|
|boolean|java.lang.Boolean|java.lang.Boolean|
|integer|java.lang.Byte, java.lang.Short, java.lang.Integer, java.lang.Long|java.lang.Long|
|decimal|java.lang.Float, java.lang.Double|java.lang.Double|
|keyword|cognitect.transit.Keyword|cognitect.transit.Keyword|
|symbol|cognitect.transit.Symbol|cognitect.transit.Symbol|
|big decimal|java.math.BigDecimal|java.math.BigDecimal|
|big integer|java.math.BigInteger|java.math.BigInteger|
|time|java.util.Date|long|
|uri|java.net.URI, cognitect.transit.URI|cognitect.transit.URI|
|uuid|java.util.UUID|java.util.UUID|
|char|java.lang.Character|java.lang.Character|
|array|Object[],primitive arrays|java.util.ArrayList|
|list|java.util.List|java.util.LinkedList|
|set|java.util.Set|java.util.HashSet|
|map|java.util.Map|java.util.HashMap|
|link|cognitect.transit.Link|cognitect.transit.Link|
|ratio +|cognitect.transit.Ratio|cognitect.transit.Ratio|

\+ Extension type

## Layered Implementations

This library is specifically designed to support layering Transit
implementations for other JVM-based languages on top of it. There are
three steps to implementing a library for a new language on top of 
this: 

- Implement WriteHandlers and ReadHandlers specific for the target
  language. Typically, WriteHandlers will be used _in addition to_ the
  ones provided by the Java library (see
  TransitFactory.defaultWriteHandlers). ReadHandlers will be used _in 
  place of_ some of the ones provided by the Java Libary (see
  TransitFactory.defaultReadHandlers). 
  
- Implement a factory API to create Readers and Writers. In general,
  Readers and Writers encapsulate the stream they work with. The APIs
  should enable an application to provide custom WriteHandlers and
  ReadHandlers, which get merged with the ones defined by the new
  library as well as the defaults provided by the Java library. The
  Reader API should also provide a way to specify a default behavior
  if no ReadHandler is available for a specific Transit value (see
  com.cognitect.transit.DefaultReadHandler). The factory API should
  delegate to TransitFactory to create Readers and Writers with the
  correct options.
  
- Implement a MapBuilder and an ArrayBuilder for unmarshaling these
  Transit ground types into objects appropriate for the target
  language. In the factory API for creating Readers, use the Reader's
  com.cognitect.transit.impl.ReaderSPI interface to attach instances
  of the new library's custom MapBuilder and ArrayBuilder
  implementations to a Reader before returning it. This must be done
  before the Reader instance is used to read data.
  
  N.B. The ReaderSPI interface is in an impl package because it is only
  intended to be used by layered Transit libraries, not by
  applications using Transit.
  
The [Clojure Transit library](http://github.com/cognitect/transit-clj)
is implemented using this layering approach and can be used as an
example of how to implement support for additional JVM languages
without having to implement all of Transit from scratch.

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
