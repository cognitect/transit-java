// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public interface ArrayReader {
    /**
     * Initializes a new array reader
     * @return a new array reader
     */
    Object init();

    /**
     * Initializes a new array reader of specified size
     * @param size initial size of the new array reader
     * @return a new array reader
     */
    Object init(int size);

    /**
     * Adds an item to the array reader, returning
     * a new array reader; new array reader must be used for
     * any further invocations
     * @param ar a map reader
     * @param item an item
     * @return a new array reader
     */
    Object add(Object ar, Object item);

    /**
     * Completes building of an object from an array
     * @param ar the array reader
     * @return the completed object
     */
    Object complete(Object ar);
}
