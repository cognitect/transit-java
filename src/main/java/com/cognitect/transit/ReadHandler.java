// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/** Converts a transit representation to an instance of a type
 */
public interface ReadHandler {
    /**
     * Converts a transit value to an instance of a type
     * @param rep the transit value
     * @return the converted object
     */
    Object fromRep(Object rep);

    /**
     * Provides a MapReader that a parser can use to convert
     * a map representation to an instance of a type incrementally
     * @return a MapReader
     */
    MapReader fromMapRep();

    /**
     * Provides a MapReader that a parser can use to convert
     * a map representation to an instance of a type incrementally
     * @return an ArrayReader
     */
    ArrayReader fromArrayRep();
}
