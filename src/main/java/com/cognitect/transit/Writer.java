// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Interface for writing values in transit format.
 */
public interface Writer {
    /**
     * Writes a single value to an output stream
     * @param o the value to write
     */
    void write(Object o);
}
