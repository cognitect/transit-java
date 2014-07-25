// Copyright 2014 Cognitect. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cognitect.transit;

/**
 * Converts an instance of an type to a transit representation
 */
public interface WriteHandler <T, Rep> {

    /**
     * Gets the tag to use for the object
     * @param o the object
     * @return tag
     */
    String tag(T o);

    /**
     * Gets the representation to use for the object, either an instance of transit ground type,
     * or object for which there is a Handler (including an instance of TaggedValue).
     * @param o the object
     * @return the representation
     */
    Rep rep(T o);

    /**
     * Gets the string representation to use for the object; can return null
     * @param o the object
     * @return the string representation
     */
    String stringRep(T o);

    /**
     * Gets an alternative handler which provides more readable representations for use in
     * verbose mode; can return null
     * @return a handler
     */
    <V> WriteHandler<T, V> getVerboseHandler();
}
