// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public interface MapReader {
    /**
     * Initializes a new map reader
     * @return a new map reader
     */
    Object init();

    /**
     * Initializes a new map reader of specified size
     * @param size initial size of the new map reader
     * @return a new map reader
     */
    Object init(int size);

    /**
     * Adds a key and value to the map reader, returning
     * a new map reader; new map reader must be used for
     * any further invocations
     * @param mr a map reader
     * @param key a key
     * @param val a value
     * @return a new map reader
     */
    Object add(Object mr, Object key, Object val);

    /**
     * Completes building of an object from a map
     * @param mr a map reader
     * @return the completed object
     */
    Object complete(Object mr);
}
