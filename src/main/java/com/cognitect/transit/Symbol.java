// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Represents a symbol
 */
public interface Symbol extends Comparable<Symbol> {
    /**
     * Gets the value of the symbol, a string
     * @return symbol
     */
    public String getValue();
}
