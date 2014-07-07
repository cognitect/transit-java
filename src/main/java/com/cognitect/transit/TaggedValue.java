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
 * Represents a transit tag and value. Returned by default when a reader encounters a tag for
 * which there is no registered ReadHandler. Can also be used in a custom WriteHandler implementation
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
