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
                         Map<String, Decoder> decoders,
                         DefaultDecoder defaultDecoder,
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
            return parseVal(false, cache);
        }
        catch (java.io.EOFException eof) {}

        return null;
    }

    @Override
    public Object parseVal(boolean asMapKey, ReadCache cache) throws IOException {
        switch (mp.getNextType()) {
            case MAP:
                return parseTaggedMap((Map) parseMap(asMapKey, cache));
            case ARRAY:
                return parseArray(asMapKey, cache);
            case RAW:
                return parseString(cache.cacheRead(mp.readValue().asRawValue().getString(), asMapKey));
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
    public Object parseMap(boolean ignored, ReadCache cache) throws IOException {

	    int sz = this.mp.readMapBegin();

        Object mb = mapBuilder.init(sz);

        for (int remainder = sz; remainder > 0; remainder--) {
            Object key = parseVal(true, cache);
            Object val = parseVal(false, cache);

            mb = mapBuilder.add(mb, key, val);
        }

        this.mp.readMapEnd(true);

        return mapBuilder.map(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache) throws IOException {

	    int sz = this.mp.readArrayBegin();

        Object ab = arrayBuilder.init(sz);

        for (int remainder = sz;remainder > 0; remainder--) {
            ab = arrayBuilder.add(ab, parseVal(false, cache));
        }

        this.mp.readArrayEnd();

        return arrayBuilder.array(ab);
    }
}
