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
 * Provides an ArrayReader to Transit to use in incrementally parsing
 * an array representation of a value.
 */
public interface ArrayReadHandler extends ReadHandler {

    /**
     * Provides an ArrayReader that a parser can use to convert
     * an array representation to an instance of a type incrementally
     * @return an ArrayReader
     */
    ArrayReader arrayReader();
}
