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

package com.cognitect.transit.impl;

import com.cognitect.transit.ReadHandler;

import java.util.*;

public class ReadHandlerMap implements Map<String, ReadHandler<?, ?>> {

    private final Map<String, ReadHandler<?, ?>> handlers;

    public ReadHandlerMap(Map<String, ReadHandler<?, ?>> customHandlers) {
        this.handlers = ReaderFactory.defaultHandlers();
        if (customHandlers != null) {
            disallowOverridingGroundTypes(customHandlers);
            handlers.putAll(customHandlers);
        }
    }

    private static void disallowOverridingGroundTypes(Map<String, ReadHandler<?,?>> handlers) {
        if (handlers != null) {
            String groundTypeTags[] = {"_", "s", "?", "i", "d", "b", "'", "map", "array"};
            for (String tag : groundTypeTags) {
                if (handlers.containsKey(tag)) {
                    throw new IllegalArgumentException("Cannot override decoding for transit ground type, tag " + tag);
                }
            }
        }
    }

    private Map<String, ReadHandler<?, ?>> getUnderlyingMap() {
        return handlers;
    }

    @Override
    public int size() {
        return handlers.size();
    }

    @Override
    public boolean isEmpty() {
        return handlers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return handlers.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return handlers.containsValue(value);
    }

    @Override
    public ReadHandler<?, ?> get(Object key) {
        return handlers.get(key);
    }

    @Override
    public ReadHandler<?, ?> put(String key, ReadHandler<?, ?> value) {
        throw new UnsupportedOperationException("ReadHandlerMap is read-only");
    }

    @Override
    public ReadHandler<?, ?> remove(Object key) {
        throw new UnsupportedOperationException("ReadHandlerMap is read-only");
    }

    @Override
    public void putAll(Map<? extends String, ? extends ReadHandler<?, ?>> m) {
        throw new UnsupportedOperationException("ReadHandlerMap is read-only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("ReadHandlerMap is read-only");
    }

    @Override
    public Set<String> keySet() {
        return handlers.keySet();
    }

    @Override
    public Collection<ReadHandler<?, ?>> values() {
        return handlers.values();
    }

    @Override
    public Set<Entry<String, ReadHandler<?, ?>>> entrySet() {
        return handlers.entrySet();
    }

    @Override
    public int hashCode() {
        return handlers.hashCode();
    }

    public boolean equals(Object other) {
        return (other instanceof ReadHandlerMap) && getUnderlyingMap().equals(((ReadHandlerMap)other).getUnderlyingMap());
    }
}
