// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/** Converts a transit value to an instance of a type
 */
public interface ReadHandler {
    /**
     * Converts a transit value to an instance of a type
     * @param rep the transit value
     * @return the decoded object
     */
    Object fromRep(Object rep);
    MapBuilder fromMapRep();
    ArrayBuilder fromArrayRep();
}
