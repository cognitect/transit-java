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

import com.cognitect.transit.WriteHandler;

import java.util.*;

public class WriteHandlerMap implements TagProvider, Map<Class, WriteHandler<?, ?>> {

    private final Map<Class, WriteHandler<?, ?>> handlers;
    private WriteHandlerMap verboseHandlerMap;

    public WriteHandlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        this.handlers = WriterFactory.defaultHandlers();
        if (customHandlers != null) {
            handlers.putAll(customHandlers);
        }
        setTagProvider(handlers);
    }

    public WriteHandlerMap verboseWriteHandlerMap() {
        if (verboseHandlerMap == null) {
            Map<Class, WriteHandler<?, ?>> verboseHandlers = new HashMap<Class, WriteHandler<?, ?>>(handlers.size());
            for (Map.Entry<Class, WriteHandler<?, ?>> entry : handlers.entrySet()) {
                WriteHandler<?, ?> verboseHandler = entry.getValue().getVerboseHandler();
                verboseHandlers.put(
                        entry.getKey(),
                        (verboseHandler == null) ? entry.getValue() : verboseHandler);
            }
            this.verboseHandlerMap = new WriteHandlerMap(verboseHandlers);
        }
        return this.verboseHandlerMap;
    }

    private void setTagProvider(Map<Class, WriteHandler<?,?>> handlers) {
        Iterator<WriteHandler<?,?>> i = handlers.values().iterator();
        while(i.hasNext()) {
            WriteHandler h = i.next();
            if(h instanceof TagProviderAware)
                ((TagProviderAware)h).setTagProvider(this);
        }
    }

    private Map<Class, WriteHandler<?, ?>> getUnderlyingMap() {
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
    public WriteHandler<?, ?> get(Object key) {
        return handlers.get(key);
    }

    @Override
    public WriteHandler<?, ?> put(Class key, WriteHandler<?, ?> value) {
        throw new UnsupportedOperationException("WriteHandlerMap is read-only");
    }

    @Override
    public WriteHandler<?, ?> remove(Object key) {
        throw new UnsupportedOperationException("WriteHandlerMap is read-only");
    }

    @Override
    public void putAll(Map<? extends Class, ? extends WriteHandler<?, ?>> m) {
        throw new UnsupportedOperationException("WriteHandlerMap is read-only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("WriteHandlerMap is read-only");
    }

    @Override
    public Set<Class> keySet() {
        return handlers.keySet();
    }

    @Override
    public Collection<WriteHandler<?, ?>> values() {
        return handlers.values();
    }

    @Override
    public Set<Entry<Class, WriteHandler<?, ?>>> entrySet() {
        return handlers.entrySet();
    }

    @Override
    public int hashCode() {
        return handlers.hashCode();
    }

    public boolean equals(Object other) {
        return (other instanceof WriteHandlerMap) && getUnderlyingMap().equals(((WriteHandlerMap)other).getUnderlyingMap());
    }

    public WriteHandler<Object,Object> getHandler(Object o) {
        Class c = (o != null) ? o.getClass() : null;
        WriteHandler<?, ?> h = null;

        if(h == null) h = handlers.get(c);
        if(h == null) h = checkBaseClasses(c);
        if(h == null) h = checkBaseInterfaces(c);

        return (WriteHandler<Object, Object>) h;
    }

    private WriteHandler<?,?> checkBaseClasses(Class c) {
        for(Class base = c.getSuperclass(); base != Object.class; base = base.getSuperclass()) {
            WriteHandler<?, ?> h = handlers.get(base);
            if(h != null) {
                handlers.put(c, h);
                return h;
            }
        }
        return null;
    }

    private WriteHandler<?,?> checkBaseInterfaces(Class c) {
        Map<Class, WriteHandler<?,?>> possibles = new HashMap<Class,WriteHandler<?,?>>();
        for (Class base = c; base != Object.class; base = base.getSuperclass()) {
            for (Class itf : base.getInterfaces()) {
                WriteHandler<?, ?> h = handlers.get(itf);
                if (h != null) possibles.put(itf, h);
            }
        }
        switch (possibles.size()) {
            case 0: return null;
            case 1: {
                WriteHandler<?, ?> h = possibles.values().iterator().next();
                handlers.put(c, h);
                return h;
            }
            default: throw new RuntimeException("More thane one match for " + c);
        }
    }

    @Override
    public String getTag(Object o) {
        WriteHandler<Object,Object> h = getHandler(o);
        if (h == null) return null;
        return h.tag(o);
    }
}
