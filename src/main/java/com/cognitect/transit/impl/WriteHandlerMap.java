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

import com.cognitect.transit.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class WriteHandlerMap implements TagProvider, Map<Class, WriteHandler<?, ?>> {

    private static Map<Class, WriteHandler<?,?>> defaultHandlers() {

        Map<Class, WriteHandler<?,?>> handlers = new HashMap<Class, WriteHandler<?,?>>();

        WriteHandler integerHandler = new WriteHandlers.IntegerWriteHandler();
        WriteHandler uriHandler = new WriteHandlers.ToStringWriteHandler("r");
        WriteHandler arrayHandler = new WriteHandlers.ArrayWriteHandler();

        handlers.put(Boolean.class, new WriteHandlers.BooleanWriteHandler());
        handlers.put(null, new WriteHandlers.NullWriteHandler());
        handlers.put(String.class, new WriteHandlers.ToStringWriteHandler("s"));
        handlers.put(Integer.class, integerHandler);
        handlers.put(Long.class, integerHandler);
        handlers.put(Short.class, integerHandler);
        handlers.put(Byte.class, integerHandler);
        handlers.put(BigInteger.class, new WriteHandlers.ToStringWriteHandler("n"));
        handlers.put(Float.class, new WriteHandlers.FloatWriteHandler());
        handlers.put(Double.class, new WriteHandlers.DoubleWriteHandler());
        handlers.put(BigDecimal.class, new WriteHandlers.ToStringWriteHandler("f"));
        handlers.put(Character.class, new WriteHandlers.ToStringWriteHandler("c"));
        handlers.put(Keyword.class, new WriteHandlers.KeywordWriteHandler());
        handlers.put(Symbol.class, new WriteHandlers.ToStringWriteHandler("$"));
        handlers.put(byte[].class, new WriteHandlers.BinaryWriteHandler());
        handlers.put(UUID.class, new WriteHandlers.UUIDWriteHandler());
        handlers.put(java.net.URI.class, uriHandler);
        handlers.put(com.cognitect.transit.URI.class, uriHandler);
        handlers.put(List.class, new WriteHandlers.ListWriteHandler());
        handlers.put(Object[].class, arrayHandler);
        handlers.put(int[].class, arrayHandler);
        handlers.put(long[].class, arrayHandler);
        handlers.put(float[].class, arrayHandler);
        handlers.put(double[].class, arrayHandler);
        handlers.put(short[].class, arrayHandler);
        handlers.put(boolean[].class, arrayHandler);
        handlers.put(char[].class, arrayHandler);
        handlers.put(Set.class, new WriteHandlers.SetWriteHandler());
        handlers.put(Date.class, new WriteHandlers.TimeWriteHandler());
        handlers.put(Ratio.class, new WriteHandlers.RatioWriteHandler());
        handlers.put(LinkImpl.class, new WriteHandlers.LinkWriteHandler());
        handlers.put(Quote.class, new WriteHandlers.QuoteAbstractEmitter());
        handlers.put(TaggedValue.class, new WriteHandlers.TaggedValueWriteHandler());

        return Collections.unmodifiableMap(handlers);
    }

    public final static Map<Class, WriteHandler<?, ?>> defaults = defaultHandlers();

    private final Map<Class, WriteHandler<?, ?>> handlers;
    private WriteHandlerMap verboseHandlerMap;
    Function<Object, Object> transform = null;

    public WriteHandlerMap() {
        this(null);
    }

    public WriteHandlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        handlers = new HashMap<Class, WriteHandler<?, ?>>();
        if (customHandlers instanceof WriteHandlerMap) {
            handlers.putAll(customHandlers);
        } else {
            handlers.putAll(defaults);
            if (customHandlers != null) {
                handlers.putAll(customHandlers);
            }
        }
        handlers.put(Map.class, new WriteHandlers.MapWriteHandler());
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
            verboseHandlerMap = new WriteHandlerMap(verboseHandlers);
        }
        return verboseHandlerMap;
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
            default: throw new RuntimeException("More than one match for " + c);
        }
    }

    @Override
    public String getTag(Object o) {
        WriteHandler<Object,Object> h = getHandler(o);
        if (h == null) return null;
        return h.tag(o);
    }

    @Override
    public String getTagAfterPossibleTransform(Object o) {
        if (transform != null)
            return this.getTag(transform.apply(o));
        else
            return this.getTag(o);
    }
}
