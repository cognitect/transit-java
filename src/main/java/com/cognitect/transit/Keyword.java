// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Represents a keyword
 */
public interface Keyword extends Comparable<Keyword> {
    /**
     * Gets the value of the keyword, a string
     * @return keyword
     */
    public String getValue();
}
