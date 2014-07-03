// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
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
                         MapBuilder mapBuilder, ListBuilder listBuilder,
                         ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
        super(decoders, defaultDecoder, mapBuilder, listBuilder, arrayBuilder, setBuilder);
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
                return parseTaggedMap((Map) parseMap(asMapKey, cache, handler));
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

        Object mb = null;

        if (handler != null) {
            MapBuilder mapBuilder = handler.fromMapRep();
            if (mapBuilder != null) {
                mb = mapBuilder.init(sz);
            }
        }

        if (mb == null) {
            mb = mapBuilder.init(sz);
        }

        ReadHandler val_handler = null;

        for (int remainder = sz; remainder > 0; remainder--) {
            Object key = parseVal(true, cache, null);

            // if this is a tagged map, find the ReadHandler for the tag
            // and pass it to value parse
            if (sz == 1 && (key instanceof String)) {
                String keyString = (String) key;
                if (keyString.length() > 1 && keyString.charAt(1) == Constants.TAG) {
                    val_handler = handlers.get(keyString.substring(2));
                }
            }

            Object val = parseVal(false, cache, val_handler);

            mb = mapBuilder.add(mb, key, val);
        }

        this.mp.readMapEnd(true);

        return mapBuilder.map(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache, ReadHandler handler) throws IOException {

	    int sz = this.mp.readArrayBegin();

        Object ab = null;

        if (handler != null) {
            ArrayBuilder arrayBuilder = handler.fromArrayRep();
            if (arrayBuilder != null) {
                ab = arrayBuilder.init(sz);
            }
        }

        if (ab == null) {
            ab = arrayBuilder.init(sz);
        }

        for (int remainder = sz;remainder > 0; remainder--) {
            ab = arrayBuilder.add(ab, parseVal(false, cache, null));
        }

        this.mp.readArrayEnd();

        return arrayBuilder.array(ab);
    }
}
