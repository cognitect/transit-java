// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Converts an instance of an type to a transit value
 */
public interface Handler {

    /**
     * Gets the tag to use for the object
     * @param o the object
     * @return tag
     */
    String getTag(Object o);

    /**
     * Gets the representation to use for the object, either an instance of transit ground type,
     * or object for which there is a Handler (including an instance of TaggedValue).
     * @param o the object
     * @return the representation
     */
    Object getRep(Object o);

    /**
     * Gets the string representation to use for the object; can return null
     * @param o the object
     * @return the string representation
     */
    String getStringRep(Object o);

    /**
     * Gets an alternative handler which provides more readable representations for use in
     * verbose mode; can return null
     * @return a handler
     */
    Handler getVerboseHandler();
}
