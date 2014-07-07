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

public interface ArrayReader {
    /**
     * Initializes a new array reader
     * @return a new array reader
     */
    Object init();

    /**
     * Initializes a new array reader of specified size
     * @param size initial size of the new array reader
     * @return a new array reader
     */
    Object init(int size);

    /**
     * Adds an item to the array reader, returning
     * a new array reader; new array reader must be used for
     * any further invocations
     * @param ar a map reader
     * @param item an item
     * @return a new array reader
     */
    Object add(Object ar, Object item);

    /**
     * Completes building of an object from an array
     * @param ar the array reader
     * @return the completed object
     */
    Object complete(Object ar);
}
