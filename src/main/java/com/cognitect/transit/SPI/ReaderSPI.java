// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.SPI;

import com.cognitect.transit.ArrayReader;
import com.cognitect.transit.MapReader;
import com.cognitect.transit.Reader;

import java.util.List;
import java.util.Map;

/**
 * Interface for providing custom MapReader and ArrayReader implementations for a Reader to use
 * when parsing native JSON or msgpack composite structures. This entry point exists to
 * enable Transit libraries for other JVM languages to layer on top of the Java Transit library,
 * but still get language-appropriate maps and arrays returned from a parse, while ensuring that
 * parsing and decoding work correctly. This interface should never be used by applications
 * that using this library.
 */
public interface ReaderSPI {
    /**
     * Specifies a custom MapReader and ArrayReader to use when parsing native maps and arrays
     * in JSON or msgpack. Implementations must accept any type of input and must return a maps
     * or lists of any type of content. This function must be called before Reader.read is called.
     * @param mapBuilder a custom MapReader that produces a Map of objects to objects
     * @param listBuilder a custom ArrayReader that yields a List of objects
     * @return
     */
    public Reader setBuilders(MapReader<?, Map<Object, Object>, Object, Object> mapBuilder,
                              ArrayReader<?, List<Object>, Object> listBuilder);
}
