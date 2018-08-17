# transit-java 0.8

### 0.8.337 / 2018-08-17 

* Add explicit jaxb-api dependency for Java 9+ (issue #28)

### 0.8.332 / 2018-03-30

* Add support for transform function in writer
* Compile to Java 1.8 target (previously 1.6)

### 0.8.327 / 2017-03-31

* Add support for defaultWriteHandler in TransitFactory.writer()

### 0.8.324 / 2017-03-13

* Update jackson-core dependency from 2.3.2 to 2.8.7

### 0.8.319 / 2016-11-18

* Fix double check locking of non-thread-safe handler caches

### 0.8.316 / 2016-09-30

* WriteHandler thread safety improvements

### 0.8.313 / 2016-07-27

* Updated msgpack dependency to 0.6.12

### 0.8.311 / 2015-10-22

* Simplified commons-codec dependency to be more direct
* Simplified jackson-core dependency to reduce transitive dependencies (#11)

### 0.8.307 / 2015-10-09

* Fixed #15: Wrap SimpleDateFormat in ThreadLocal

### 0.8.304 / 2015-08-07

* ReadHandlerMap and WriteHandlerMap

### 0.8.285 / 2015-03-13

* cache read and write handlers
