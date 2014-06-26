// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.util.List;

/**
 * Abstracts functional API for building a list
 */
public interface ListBuilder {
    /**
     * Initializes a new list builder
     * @return a new list builder
     */
    Object init();

    /**
     * Initializes a new list builder of specified size
     * @param size initial size of the new list builder
     * @return a new list builder
     */
    Object init(int size);

    /**
     * Adds an item to the specified list builder, returning
     * the new list builder.
     * @param lb the list builder to add to, returned from init
     * @param item the item to add
     * @return the new list builder
     */
    Object add(Object lb, Object item);

    /**
     * Completes building of a list
     * @param lb the list builder
     * @return the completed list
     */
    List list(Object lb);
}
