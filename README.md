# transit-java

Transit is a data format and a set of libraries for conveying values between applications written in different languages. This library provides support for marshalling Transit data to/from Java.

* [Rationale](https://blog.cognitect.com/blog/2014/7/22/transit)
* [API docs](https://cognitect.github.io/transit-java/)
* [Specification](https://github.com/cognitect/transit-format)

This implementation's major.minor version number corresponds to the version of the Transit specification it supports.

_NOTE: Transit is intended primarily as a wire protocol for transferring data between applications. If storing Transit data durably, readers and writers are expected to use the same version of Transit and you are responsible for migrating/transforming/re-storing that data when and if the transit format changes._

## Releases and Dependency Information

* Latest release: 1.0.371
* [All Released Versions](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cognitect%22%20AND%20a%3A%22transit-java%22)

[Maven](https://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>com.cognitect</groupId>
  <artifactId>transit-java</artifactId>
  <version>1.0.371</version>
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
OutputStream out = new ByteArrayOutputStream();
Writer writer = TransitFactory.writer(TransitFactory.Format.MSGPACK, out);
writer.write(data);

// Read the data from a stream
InputStream in = new ByteArrayInputStream(out.toByteArray());
Reader reader = TransitFactory.reader(TransitFactory.Format.MSGPACK, in);
Object data = reader.read();
```

### Custom write handler

```java
public class Point {
    public final int x;
    public final int y;
    public Point(int x,int y) {this.x = x; this.y = y;}
    public String toString() { return "Point at " + x + ", " + y; }
    public boolean equals(Object other) { return other instanceof Point &&
            ((Point)other).x == x &&
            ((Point)other).y == y; }
    public int hashCode() { return x * y; }
}

Map<Class, WriteHandler<?,?>> customHandlers = new HashMap<Class, WriteHandler<?,?>>(){{
    put(Point.class, new WriteHandler() {
        @Override
        public String tag(Object o) { return "point"; }
        @Override
        public Object rep(Object o) { return Arrays.asList(((Point)o).x, ((Point)o).y); }
        @Override
        public String stringRep(Object o) { return rep(o).toString(); }
        @Override
        public WriteHandler getVerboseHandler() { return this; }
    });
}};
OutputStream out = new ByteArrayOutputStream();
Writer w = TransitFactory.writer(TransitFactory.Format.JSON, out, TransitFactory.writeHandlerMap(customHandlers));
w.write(new Point(37, 42));
System.out.print(out.toString());
;; => ["~#point",[37,42]]
```

### Custom read handler

```java
Map<String, ReadHandler<?, ?>> customHandlers = new HashMap<String, ReadHandler<?, ?>>() {{
    put("point", new ReadHandler() {
        @Override
        public Object fromRep(Object o) {
            List coords = (List) o;
            int x = ((Long) coords.get(0)).intValue();
            int y = ((Long) coords.get(1)).intValue();
            return new Point(x,y);
        }
    });
}};
InputStream in = new ByteArrayInputStream("[\"~#point\",[37,42]]".getBytes());
Reader reader = TransitFactory.reader(TransitFactory.Format.JSON, in, customHandlers);
System.out.print(reader.read());
// => Point at 37, 42
```

### Custom default write handler

```java
WriteHandler customDefaultWriteHandler = new WriteHandler() {
    @Override
    public String tag(Object o) { return "unknown"; }
    @Override
    public Object rep(Object o) { return o.toString(); }
    @Override
    public String stringRep(Object o) { return o.toString(); }
    @Override
    public WriteHandler getVerboseHandler() { return this; }
};
OutputStream out = new ByteArrayOutputStream();
Writer w = TransitFactory.writer(TransitFactory.Format.JSON, out, customDefaultWriteHandler);
w.write(new Point(37,42));
System.out.print(out.toString());
// => "[\"~#unknown\",\"Point at 37, 42\"]"
```

### Custom default read handler

```java
DefaultReadHandler readHandler = new DefaultReadHandler() {
    @Override
    public Object fromRep(String tag, Object rep) {
        return tag + ": " + rep.toString();
    }
};
InputStream in = new ByteArrayInputStream("[\"~#unknown\",[37,42]]".getBytes());
Reader reader = TransitFactory.reader(TransitFactory.Format.JSON, in, readHandler);
System.out.print(reader.read());
// => unknown: [37, 42]
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
  
- Implement a MapReader and an ArrayReader for unmarshaling these
  Transit ground types into objects appropriate for the target
  language. In the factory API for creating Readers, use each new Reader's
  com.cognitect.transit.SPI.ReaderSPI interface to attach instances
  of the new library's custom MapReader and ArrayReader
  implementations to a Reader before returning it. This must be done
  before the Reader instance is used to read data.
  
  N.B. The ReaderSPI interface is in an impl package because it is only
  intended to be used by layered Transit libraries, not by
  applications using Transit.
  
The [Clojure Transit library](https://github.com/cognitect/transit-clj)
is implemented using this layering approach and can be used as an
example of how to implement support for additional JVM languages
without having to implement all of Transit from scratch.

## Contributing 

This library is open source, developed internally by Cognitect. We welcome discussions of potential problems and enhancement suggestions on the [transit-format mailing list](https://groups.google.com/forum/#!forum/transit-format). Issues can be filed using GitHub [issues](https://github.com/cognitect/transit-java/issues) for this project. Because transit is incorporated into products and client projects, we prefer to do development internally and are not accepting pull requests or patches.

## Copyright and License

Copyright © 2014 Cognitect

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
