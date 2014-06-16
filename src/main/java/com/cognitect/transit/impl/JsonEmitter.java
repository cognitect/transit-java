// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

public class JsonEmitter extends AbstractEmitter {

    private final static BigInteger JSON_INT_MAX = new BigInteger(String.valueOf((long) Math.pow(2, 53)));
    private final static BigInteger JSON_INT_MIN = new BigInteger("-" + JSON_INT_MAX.toString());

    protected final JsonGenerator gen;

    public JsonEmitter(JsonGenerator gen, Map<Class, Handler> handlers) {
        super(handlers);
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
            emitString(Constants.ESC_STR,"?", b.toString().substring(0, 1), asMapKey, cache);
        }
        else {
            gen.writeBoolean(b);
        }
    }

    @Override
    public void emitInteger(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        if(o instanceof BigInteger) {
            BigInteger bi = (BigInteger)o;
            if(asMapKey || bi.compareTo(JSON_INT_MAX) > 0 || bi.compareTo(JSON_INT_MIN) < 0)
                emitString(Constants.ESC_STR, "i", bi.toString(), asMapKey, cache);
            else
                gen.writeNumber(bi.longValue());
        }
        else {
            long i = Util.numberToPrimitiveLong(o);

            if(asMapKey || i > JSON_INT_MAX.longValue() || i < JSON_INT_MIN.longValue())
                emitString(Constants.ESC_STR, "i", String.valueOf(i), asMapKey, cache);
            else
                gen.writeNumber(i);
        }
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
    public void emitBinary(Object b, boolean asMapKey, WriteCache cache) throws Exception {

        byte[] encodedBytes = Base64.encodeBase64((byte[])b);
        emitString(Constants.ESC_STR, "b", new String(encodedBytes), asMapKey, cache);
    }

    @Override
    public void emitQuoted(Object o, WriteCache cache) throws Exception {
        emitMapStart(1L);
        gen.writeFieldName(Constants.QUOTE_TAG);
        marshal(o, false, cache);
        emitMapEnd();
    }

    @Override
    protected void emitTaggedMap(String t, Object o, boolean ignored, WriteCache cache) throws Exception {
        String outString = cache.cacheWrite(Util.maybePrefix(Constants.ESC_TAG, t, ""), true);
        emitMapStart(1L);
        gen.writeFieldName(outString);
        marshal(o, false, cache);
        emitMapEnd();
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
    protected void emitMap(Object o, boolean ignored, WriteCache cache) throws Exception {

        Iterable<Map.Entry> i = ((Iterable<Map.Entry>) o);
        long sz = Util.mapSize(i);

        emitArrayStart(sz);
        marshal("^ ", false, cache);

        for (Map.Entry e : i) {
            marshal(e.getKey(), true, cache);
            marshal(e.getValue(), false, cache);
        }
        emitArrayEnd();
    }
}
