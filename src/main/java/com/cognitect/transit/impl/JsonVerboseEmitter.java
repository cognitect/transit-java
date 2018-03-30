// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;
import com.fasterxml.jackson.core.JsonGenerator;

import java.util.Map;
import java.util.function.Function;

public class JsonVerboseEmitter extends JsonEmitter {

    @Deprecated
    public JsonVerboseEmitter(JsonGenerator gen, WriteHandlerMap writeHandlerMap) {
        super(gen, writeHandlerMap, null);
    }

    public JsonVerboseEmitter(JsonGenerator gen, WriteHandlerMap writeHandlerMap, WriteHandler defaultWriteHandler) {
        super(gen, writeHandlerMap, defaultWriteHandler);
    }

    public JsonVerboseEmitter(JsonGenerator gen, WriteHandlerMap writeHandlerMap, WriteHandler defaultWriteHandler, Function<Object,Object> transform) {
        super(gen, writeHandlerMap, defaultWriteHandler, transform);
    }

    @Override
    public void emitString(String prefix, String tag, String s, boolean asMapKey, WriteCache cache) throws Exception {
        String outString = cache.cacheWrite(Util.maybePrefix(prefix, tag, s), asMapKey);
        if(asMapKey)
            gen.writeFieldName(outString);
        else
            gen.writeString(outString);
    }

    @Override
    protected void emitTagged(String t, Object o, boolean ignored, WriteCache cache) throws Exception {
        emitMapStart(1L);
        emitString(Constants.ESC_TAG, t, "", true, cache);
        marshal(o, false, cache);
        emitMapEnd();
    }

    @Override
    protected void emitMap(Iterable<Map.Entry<Object, Object>> i, boolean ignored, WriteCache cache) throws Exception {

        long sz = Util.mapSize(i);

        emitMapStart(sz);
        for (Map.Entry e : i) {
            marshal(e.getKey(), true, cache);
            marshal(e.getValue(), false, cache);
        }
        emitMapEnd();
    }
}
