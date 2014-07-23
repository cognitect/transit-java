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

public interface MapReader<G, M, K, V> {
    /**
     * Initializes a new (gestational) map
     * @return a new map
     */
    G init();

    /**
     * Initializes a new (gestational) map of specified size
     * @param size initial size of the new map
     * @return a new map
     */
    G init(int size);

    /**
     * Adds a key and value to the map, returning
     * a new map; new map must be used for
     * any further invocations
     * @param m a (gestational) map
     * @param key a key
     * @param val a value
     * @return a new gestational map
     */
    G add(G m, K key, V val);

    /**
     * Completes building of a map
     * @param m a map
     * @return the completed map
     */
    M complete(G m);
}
