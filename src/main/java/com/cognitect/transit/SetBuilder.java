// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.util.Set;

/**
 * Abstracts functional API for building a set
 */
public interface SetBuilder {
    /**
     * Initializes a new set builder
     * @return a new set builder
     */
    Object init();

    /**
     * Initializes a new set builder of specified size
     * @param size initial size for the new set
     * @return a new set builder
     */
    Object init(int size);

    /**
     * Adds an item to the specified set builder, returning
     * the new set builder
     * @param sb the set builder to add to, returned from init
     * @param item the item to add
     * @return the new set builder
     */
    Object add(Object sb, Object item);

    /**
     * Completes building of a set
     * @param sb a set builder
     * @return the completed set
     */
    Set set(Object sb);
}
