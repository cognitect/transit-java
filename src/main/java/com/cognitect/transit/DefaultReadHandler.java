// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Processes a non-decodable transit value
 */
public interface DefaultReadHandler {
    /**
     * Reads a transit representation that cannot otherwise be read
     * @param tag the transit value's tag
     * @param rep the transit value's representation
     * @return the resulting generic object
     */
    Object fromRep(String tag, Object rep);
}
