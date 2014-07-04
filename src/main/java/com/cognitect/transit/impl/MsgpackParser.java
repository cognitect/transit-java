// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;
import com.cognitect.transit.DefaultReadHandler;
import com.cognitect.transit.MapReader;
import com.cognitect.transit.ReadHandler;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;


public class MsgpackParser extends AbstractParser {
    private final Unpacker mp;

    public MsgpackParser(Unpacker mp,
                         Map<String, ReadHandler> decoders,
                         DefaultReadHandler defaultDecoder,
                         MapBuilder mapBuilder,
                         ArrayBuilder arrayBuilder) {
        super(decoders, defaultDecoder, mapBuilder, arrayBuilder);
        this.mp = mp;
    }

    private Object parseLong() throws IOException {
        Value val = mp.readValue();

        try {
            Long l = val.asIntegerValue().getLong();
            return l;
        }
        catch (Exception ex) {
            BigInteger bi = new BigInteger(val.asRawValue().getString());
        }

        return val;
    }

    @Override
    public Object parse(ReadCache cache) throws IOException {
        try {
            return parseVal(false, cache, null);
        }
        catch (java.io.EOFException eof) {}

        return null;
    }

    @Override
    public Object parseVal(boolean asMapKey, ReadCache cache, ReadHandler handler) throws IOException {
        switch (mp.getNextType()) {
            case MAP:
                return parseTaggedMap(parseMap(asMapKey, cache, handler));
            case ARRAY:
                return parseArray(asMapKey, cache, handler);
            case RAW:
                return cache.cacheRead(mp.readValue().asRawValue().getString(), asMapKey, this);
            case INTEGER:
                return parseLong();
            case FLOAT:
                return mp.readValue().asFloatValue().getDouble();
            case BOOLEAN:
                return mp.readValue().asBooleanValue().getBoolean();
            case NIL:
                mp.readNil();
        }

        return null;
    }

    @Override
    public Object parseMap(boolean ignored, ReadCache cache, ReadHandler handler) throws IOException {

	    int sz = this.mp.readMapBegin();

        MapReader mr = null;

        if (handler != null) {
            mr = handler.fromMapRep();
        }

        if (mr == null) {
            mr = mapBuilder;
        }

        Object mb = mr.init(sz);

        ReadHandler val_handler = null;

        boolean tagged = false;
        Object val = null;

        for (int remainder = sz; remainder > 0; remainder--) {
            Object key = parseVal(true, cache, null);

            // if this is a tagged map, find the ReadHandler for the tag
            // and pass it to value parse
            if (sz == 1 && (key instanceof String)) {
                String keyString = (String) key;
                if (keyString.length() > 1 && keyString.charAt(1) == Constants.TAG) {
                    tagged = true;
                    val_handler = handlers.get(keyString.substring(2));
                }
            }

            val = parseVal(false, cache, val_handler);

            mb = mr.add(mb, key, val);
        }

        this.mp.readMapEnd(true);

        return mr.complete(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache, ReadHandler handler) throws IOException {

	    int sz = this.mp.readArrayBegin();

        ArrayReader ar = null;
        if (handler != null) {
            ar = handler.fromArrayRep();
        }
        if (ar == null) {
            ar = arrayBuilder;
        }

        Object ab = ar.init(sz);
        for (int remainder = sz;remainder > 0; remainder--) {
            ab = ar.add(ab, parseVal(false, cache, null));
        }
        this.mp.readArrayEnd();

        return ar.complete(ab);
    }
}
