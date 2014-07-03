// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.util.List;

/**
 * Abstracts functional API for building an array representation
 */
public interface ArrayBuilder {
    /**
     * Initializes a new array builder
     * @return a new array builder
     */
    Object init();

    /**
     * Initializes a new array builder of specified size
     * @param size initial size of the new array builder
     * @return a new array builder
     */
    Object init(int size);

    /**
     * Adds an item to the specified array builder, returning
     * the new array builder.
     * @param ab the array builder to add to, returned from init
     * @param item the item to add
     * @return the new array builder
     */
    Object add(Object ab, Object item);

    /**
     * Completes building of an array representation
     * @param ab the array builder
     * @return the completed array representation
     */
    List array(Object ab);
}
