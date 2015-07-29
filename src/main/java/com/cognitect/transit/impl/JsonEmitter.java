// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

public class JsonEmitter extends AbstractEmitter {

    private final static BigInteger JSON_INT_MAX = new BigInteger(String.valueOf((long) Math.pow(2, 53) - 1));
    private final static BigInteger JSON_INT_MIN = new BigInteger("-" + JSON_INT_MAX.toString());

    protected final JsonGenerator gen;

    public JsonEmitter(JsonGenerator gen, WriteHandlerMap writeHandlerMap) {
        super(writeHandlerMap);
        this.gen = gen;
    }

    @Override
    public void emit(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        marshalTop(o, cache);
    }

    @Override
    public void emitNil(boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey)
            emitString(Constants.ESC_STR, "_", "", asMapKey, cache);
        else
            gen.writeNull();
    }

    @Override
    public void emitString(String prefix, String tag, String s, boolean asMapKey, WriteCache cache) throws Exception {
        String outString = cache.cacheWrite(Util.maybePrefix(prefix, tag, s), asMapKey);
        gen.writeString(outString);
    }

    @Override
    public void emitBoolean(Boolean b, boolean asMapKey, WriteCache cache) throws Exception {
        if(asMapKey) {
            emitString(Constants.ESC_STR,"?", b ? "t" : "f", asMapKey, cache);
        }
        else {
            gen.writeBoolean(b);
        }
    }

    @Override
    public void emitBoolean(boolean b, boolean asMapKey, WriteCache cache) throws Exception {
        if(asMapKey) {
            emitString(Constants.ESC_STR,"?", b ? "t" : "f", asMapKey, cache);
        }
        else {
            gen.writeBoolean(b);
        }
    }

    @Override
    public void emitInteger(Object o, boolean asMapKey, WriteCache cache) throws Exception {
        long i = Util.numberToPrimitiveLong(o);
        if(asMapKey || i > JSON_INT_MAX.longValue() || i < JSON_INT_MIN.longValue())
            emitString(Constants.ESC_STR, "i", String.valueOf(i), asMapKey, cache);
        else
            gen.writeNumber(i);
    }

    @Override
    public void emitInteger(long i, boolean asMapKey, WriteCache cache) throws Exception {
        if(asMapKey || i > JSON_INT_MAX.longValue() || i < JSON_INT_MIN.longValue())
            emitString(Constants.ESC_STR, "i", String.valueOf(i), asMapKey, cache);
        else
            gen.writeNumber(i);
    }

    @Override
    public void emitDouble(Object d, boolean asMapKey, WriteCache cache) throws Exception {
        if(asMapKey)
            emitString(Constants.ESC_STR, "d", d.toString(), asMapKey, cache);
        else if(d instanceof Double)
            gen.writeNumber((Double)d);
        else if(d instanceof Float)
            gen.writeNumber((Float)d);
    }

    @Override
    public void emitDouble(float d, boolean asMapKey, WriteCache cache) throws Exception {
        if(asMapKey)
            emitString(Constants.ESC_STR, "d", String.valueOf(d), asMapKey, cache);
        else
            gen.writeNumber(d);
    }

    @Override
    public void emitDouble(double d, boolean asMapKey, WriteCache cache) throws Exception {
        if(asMapKey)
            emitString(Constants.ESC_STR, "d", String.valueOf(d), asMapKey, cache);
        else
            gen.writeNumber(d);
    }

    @Override
    public void emitBinary(Object b, boolean asMapKey, WriteCache cache) throws Exception {

        byte[] encodedBytes = Base64.encodeBase64((byte[])b);
        emitString(Constants.ESC_STR, "b", new String(encodedBytes), asMapKey, cache);
    }

    @Override
    public void emitArrayStart(Long size) throws Exception {
        gen.writeStartArray();
    }

    @Override
    public void emitArrayEnd() throws Exception {
        gen.writeEndArray();
    }

    @Override
    public void emitMapStart(Long ignored) throws Exception {
        gen.writeStartObject();
    }

    @Override
    public void emitMapEnd() throws Exception {
        gen.writeEndObject();
    }

    @Override
    public void flushWriter() throws IOException {

        gen.flush();
    }

    @Override
    public boolean prefersStrings() {

        return true;
    }

    @Override
    protected void emitMap(Iterable<Map.Entry<Object, Object>> i, boolean ignored, WriteCache cache) throws Exception {

        long sz = Util.mapSize(i);

        emitArrayStart(sz);
        emitString(null, null, Constants.MAP_AS_ARRAY, false, cache);

        for (Map.Entry e : i) {
            marshal(e.getKey(), true, cache);
            marshal(e.getValue(), false, cache);
        }
        emitArrayEnd();
    }
}
