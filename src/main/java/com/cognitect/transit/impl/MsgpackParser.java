// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import org.msgpack.type.Value;
import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;


public class MsgpackParser extends AbstractParser {
    private final Unpacker mp;

    public MsgpackParser(Unpacker mp,
                         Map<String, ReadHandler> handlers,
                         DefaultReadHandler defaultHandler,
                         MapBuilder mapBuilder,
                         ArrayBuilder arrayBuilder) {
        super(handlers, defaultHandler, mapBuilder, arrayBuilder);
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
                return parseMap(asMapKey, cache, null);
            case ARRAY:
                return parseArray(asMapKey, cache, null);
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
    public Object parseMap(boolean ignored, ReadCache cache, MapReadHandler handler) throws IOException {

	    int sz = this.mp.readMapBegin();

        MapReader mr = (handler != null) ? handler.mapReader() : mapBuilder;

        Object mb = mr.init(sz);

        ReadHandler val_handler = null;

        boolean tagged = false;
        Object val = null;

        for (int remainder = sz; remainder > 0; remainder--) {
            Object key = parseVal(true, cache);

            // if this is a tagged map, find the ReadHandler for the tag
            // and pass it to value parse
            if (sz == 1 && (key instanceof String)) {
                String keyString = (String) key;
                if (keyString.length() > 1 && keyString.charAt(1) == Constants.TAG) {
                    tagged = true;
                    val_handler = handlers.get(keyString.substring(2));
                }
            }

            if (!tagged) {
                val = parseVal(false, cache);
                mb = mr.add(mb, key, val);
            } else {
                if (val_handler != null) {
                    if (this.mp.getNextType() == ValueType.MAP && val_handler instanceof MapReadHandler) {
                        // use map reader to decode value
                        val = parseMap(false, cache, (MapReadHandler) val_handler);
                    } else if (this.mp.getNextType() == ValueType.ARRAY && val_handler instanceof ArrayReadHandler) {
                        // use array reader to decode value
                        val = parseArray(false, cache, (ArrayReadHandler) val_handler);
                    } else {
                        // read value and decode normally
                        val = val_handler.fromRep(parseVal(false, cache));
                    }
                } else {
                    // default decode
                    val = this.decode(((String)key).substring(2), parseVal(false, cache));
                }
                this.mp.readMapEnd(true);
                return val;
            }
        }

        this.mp.readMapEnd(true);
        return mr.complete(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache, ArrayReadHandler handler) throws IOException {

	    int sz = this.mp.readArrayBegin();

        ArrayReader ar = (handler != null) ? handler.arrayReader() : arrayBuilder;

        Object ab = ar.init(sz);
        for (int remainder = sz;remainder > 0; remainder--) {
            ab = ar.add(ab, parseVal(false, cache));
        }
        this.mp.readArrayEnd();

        return ar.complete(ab);
    }
}
