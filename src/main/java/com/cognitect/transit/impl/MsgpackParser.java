// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.ListBuilder;
import com.cognitect.transit.MapBuilder;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.Map;


public class MsgpackParser extends AbstractParser {
    private final Unpacker mp;

    public MsgpackParser(Unpacker mp,
                         Map<String, Decoder> decoders,
                         MapBuilder mapBuilder, ListBuilder listBuilder) {
        super(decoders, mapBuilder, listBuilder);
        this.mp = mp;
    }

    private Object parseLong() throws IOException {
        Object val;

        // TODO: BigInteger and robustness

        val = mp.readValue().asIntegerValue().getLong();

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

            mapBuilder.add(mb, key, val);
        }

        this.mp.readMapEnd(true);

        return mapBuilder.map(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache) throws IOException {

	    int sz = this.mp.readArrayBegin();

        Object lb = listBuilder.init(sz);

        for (int remainder = sz;remainder > 0; remainder--) {
            listBuilder.add(lb, parseVal(false, cache));
        }

        this.mp.readArrayEnd();

        return listBuilder.list(lb);
    }
}
