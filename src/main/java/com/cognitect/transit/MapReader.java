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

public interface MapReader {
    /**
     * Initializes a new map reader
     * @return a new map reader
     */
    Object init();

    /**
     * Initializes a new map reader of specified size
     * @param size initial size of the new map reader
     * @return a new map reader
     */
    Object init(int size);

    /**
     * Adds a key and value to the map reader, returning
     * a new map reader; new map reader must be used for
     * any further invocations
     * @param mr a map reader
     * @param key a key
     * @param val a value
     * @return a new map reader
     */
    Object add(Object mr, Object key, Object val);

    /**
     * Completes building of an object from a map
     * @param mr a map reader
     * @return the completed object
     */
    Object complete(Object mr);
}
