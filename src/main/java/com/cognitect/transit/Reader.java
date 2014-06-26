// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Interace for reading values in transit format
 */
public interface Reader {
    /**
     * Reads a single value from an input source
     * @return the value
     */
    Object read();
}
