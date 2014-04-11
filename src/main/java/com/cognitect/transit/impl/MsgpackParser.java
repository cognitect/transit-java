package com.cognitect.transit.impl;

import com.cognitect.transit.Decoder;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.MessagePackUnpacker;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.msgpack.type.Value;
import org.msgpack.type.MapValue;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.ValueType;


public class MsgpackParser extends AbstractParser {
    private final Unpacker mp;

    public MsgpackParser(Unpacker mp, Map<String, Decoder> decoders) {
        super(decoders);
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
        // TODO: This could be better

        try {
            if (mp.getNextType() != null)
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

        Map map = new HashMap();

        for (int remainder = mp.readMapBegin(); remainder > 0; remainder--) {
            Object key = parseVal(true, cache);
            Object val = parseVal(false, cache);

            map.put(key, val);
        }

        mp.readMapEnd(true);

        return map;
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache) throws IOException {

        List list = new ArrayList();

        for (int remainder = mp.readArrayBegin(); remainder > 0; remainder--) {
            list.add(parseVal(false, cache));
        }

        return list;
    }
}
