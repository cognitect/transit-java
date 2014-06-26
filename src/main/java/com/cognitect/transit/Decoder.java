// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/** Converts a transit value to an instance of a type
 */
public interface Decoder {
    /**
     * Converts a transit value to an instance of a type
     * @param encodedVal the transit value
     * @return the decoded object
     */
    Object decode(Object encodedVal);
}
