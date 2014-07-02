// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Represents a transit tag and value. Returned by default when a reader encounters a tag for
 * which there is no registered decoder. Can also be used in a custom Handler implementation
 * to force representation to use a transit ground type using a rep for which there is no
 * registered handler (e.g., an iterable for the representation of an array).
 */
public interface TaggedValue {
    /**
     * Gets the tag
     * @return tag
     */
    public String getTag();

    /**
     * Gets the representation of the value
     * @return rep
     */
    public Object getRep();
}
