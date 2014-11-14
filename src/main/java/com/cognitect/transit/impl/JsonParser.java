// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import com.fasterxml.jackson.core.JsonToken;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class JsonParser extends AbstractParser {

    private final com.fasterxml.jackson.core.JsonParser jp;

    public JsonParser(com.fasterxml.jackson.core.JsonParser jp,
                      Map<String, ReadHandler<?,?>> handlers,
                      DefaultReadHandler<?> defaultHandler,
                      MapReader<?, Map<Object, Object>, Object, Object> mapBuilder,
                      ArrayReader<?, List<Object>, Object> listBuilder) {

        super(handlers, defaultHandler, mapBuilder, listBuilder);
        this.jp = jp;
    }

    private Object parseLong() throws IOException {

        Object val;
        try {
            val = jp.getLongValue();
        }catch(IOException e) {
            val = new BigInteger(jp.getText());
        }

        return val;
    }

    @Override
    public Object parse(ReadCache cache) throws IOException {
        if(jp.nextToken() == null)
            throw new EOFException("NO NEXT TOKEN in JsonParser");
        else
            return parseVal(false, cache);
    }

    @Override
    public Object parseVal(boolean asMapKey, ReadCache cache) throws IOException {

        switch(jp.getCurrentToken()) {
            case START_OBJECT:
                return parseMap(asMapKey, cache, null);
            case START_ARRAY:
                return parseArray(asMapKey, cache, null);
            case FIELD_NAME:
                return cache.cacheRead(jp.getText(), asMapKey, this);
            case VALUE_STRING:
                return cache.cacheRead(jp.getText(), asMapKey, this);
            case VALUE_NUMBER_INT:
                return parseLong();
            case VALUE_NUMBER_FLOAT:
                return jp.getDoubleValue();
            case VALUE_TRUE:
                return true;
            case VALUE_FALSE:
                return false;
            case VALUE_NULL:
                return null;
            default: return null;
        }
    }

    @Override
    public Object parseMap(boolean ignored, ReadCache cache, MapReadHandler<Object, ?, Object, Object, ?> handler) throws IOException {
        return parseMap(ignored, cache, handler, JsonToken.END_OBJECT);
    }

    public Object parseMap(boolean ignored, ReadCache cache, MapReadHandler<Object, ?, Object, Object, ?> handler, JsonToken endToken) throws IOException {

        MapReader<Object, ?, Object, Object> mr = (handler != null) ? handler.mapReader() : mapBuilder;

        Object mb = mr.init();

        while(jp.nextToken() != endToken) {
            Object key = parseVal(true, cache);
            if (key instanceof Tag) {
                String tag = ((Tag) key).getValue();
                ReadHandler<Object, Object> val_handler = getHandler(tag);
                Object val;
                jp.nextToken(); // advance to read value
                if (val_handler != null) {
                    if (this.jp.getCurrentToken() == JsonToken.START_OBJECT && val_handler instanceof MapReadHandler) {
                        // use map reader to decode value
                        val = parseMap(false, cache, (MapReadHandler<Object, ?, Object, Object, ?>) val_handler);
                    } else if (this.jp.getCurrentToken() == JsonToken.START_ARRAY && val_handler instanceof ArrayReadHandler) {
                        // use array reader to decode value
                        val = parseArray(false, cache, (ArrayReadHandler<Object, ?, Object, ?>) val_handler);
                    } else {
                        // read value and decode normally
                        val = val_handler.fromRep(parseVal(false, cache));
                    }
                } else {
                    // default decode
                    val = this.decode(tag, parseVal(false, cache));
                }
                jp.nextToken(); // advance to read end of object or array
                return val;
            } else {
                jp.nextToken(); // advance to read value
                mb = mr.add(mb, key, parseVal(false, cache));
            }
        }

        return mr.complete(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache, ArrayReadHandler<Object, ?, Object, ?> handler) throws IOException {

        // if nextToken == JsonToken.END_ARRAY
        if (jp.nextToken() != JsonToken.END_ARRAY) {
            Object firstVal = parseVal(false, cache);
            if (firstVal != null) {
                if (firstVal == Constants.MAP_AS_ARRAY) {
                    // if the same, build a map w/ rest of array contents
                    return parseMap(false, cache, null, JsonToken.END_ARRAY);
                } else if (firstVal instanceof Tag) {
                    String tag = ((Tag) firstVal).getValue();
                    ReadHandler<Object, Object> val_handler = getHandler(tag);
                    jp.nextToken(); // advance to value
                    Object val;
                    if (val_handler != null) {
                        if (this.jp.getCurrentToken() == JsonToken.START_OBJECT && val_handler instanceof MapReadHandler) {
                            // use map reader to decode value
                            val = parseMap(false, cache, (MapReadHandler<Object, ?, Object, Object, ?>) val_handler);
                        } else if (this.jp.getCurrentToken() == JsonToken.START_ARRAY && val_handler instanceof ArrayReadHandler) {
                            // use array reader to decode value
                            val = parseArray(false, cache, (ArrayReadHandler<Object, ?, Object, ?>) val_handler);
                        } else {
                            // read value and decode normally
                            val = val_handler.fromRep(parseVal(false, cache));
                        }
                    } else {
                        // default decode
                        val = this.decode(tag, parseVal(false, cache));
                    }
                    jp.nextToken(); // advance past end of object or array
                    return val;
                }
            }

            // process array w/o special decoding or interpretation
            ArrayReader<Object, ?, Object> ar = (handler != null) ? handler.arrayReader() : listBuilder;
            Object ab = ar.init();
            ab = ar.add(ab, firstVal);
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                ab = ar.add(ab, parseVal(false, cache));
            }
            return ar.complete(ab);
        }

        // make an empty collection, honoring handler's arrayReader, if present
        ArrayReader<Object, ?, Object> ar = (handler != null) ? handler.arrayReader() : listBuilder;
        return ar.complete(ar.init(0));
    }
}
