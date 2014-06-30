// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Represents a URI
 */
public interface URI extends Comparable<URI> {
    /**
     * Gets the value of the symbol, a string
     * @return symbol
     */
    public String getValue();
}
