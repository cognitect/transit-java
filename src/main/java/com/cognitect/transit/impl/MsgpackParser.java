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

        for (int remainder = sz; remainder > 0; remainder--) {
            Object key = parseVal(true, cache);
            if (key instanceof Tag) {
                String tag = ((Tag)key).getValue();
                ReadHandler val_handler = handlers.get(tag);
                Object val;
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
                    val = this.decode(tag, parseVal(false, cache));
                }

                this.mp.readMapEnd(true);
                return val;
            } else {
                mb = mr.add(mb, key, parseVal(false, cache));
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

        for (int remainder = sz; remainder > 0; remainder--) {
            Object val = parseVal(false, cache);
            if ((val != null) && (val instanceof Tag)) {
                // it's a tagged value
                String tag = ((Tag) val).getValue();
                ReadHandler val_handler = handlers.get(tag);
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
                    val = this.decode(tag, parseVal(false, cache));
                }
                this.mp.readArrayEnd();
                return val;
            } else {
                // fall through to regular parse
                ab = ar.add(ab, val);
            }
        }

        this.mp.readArrayEnd();
        return ar.complete(ab);
    }
}
