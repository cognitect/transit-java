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

public interface ArrayReader<G,A,T> {
    /**
     * Initializes a new gestational result
     * @return a new gestational result
     */
    G init();

    /**
     * Initializes a new gestational result of specified size
     * @param size initial size of the new result
     * @return a new gestational result
     */
    G init(int size);

    /**
     * Adds an item to the result, returning
     * a new result; new result must be used for
     * any further invocations
     * @param a gestational result
     * @param item an item
     * @return a new result
     */
    G add(G a, T item);

    /**
     * Completes building of a result from a gestational result
     * @param ar the gestational result
     * @return the completed object
     */
    A complete(G ar);
}
