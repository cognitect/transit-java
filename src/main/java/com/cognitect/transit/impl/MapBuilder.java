// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.util.Map;

/**
 * Abstracts functional API for building a map
 */
public interface MapBuilder {
    /**
     * Initializes a new map builder
     * @return a new map builder
     */
    Object init();

    /**
     * Initializes a new map builder of specified size
     * @param size initial size of the new map builder
     * @return a new map builder
     */
    Object init(int size);

    /**
     * Adds an item to the specified map builder, returning
     * the new map builder.
     * @param mb the map builder to add to, returned from init
     * @param key the key to add
     * @param value the value to add
     * @return the new map builder
     */
    Object add(Object mb, Object key, Object value);

    /**
     * Completes building of a map
     * @param mb the map builder
     * @return the completed map
     */
    Map map(Object mb);
}
