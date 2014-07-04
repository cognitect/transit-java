// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;
import com.cognitect.transit.DefaultReadHandler;
import com.cognitect.transit.MapReader;
import com.cognitect.transit.ReadHandler;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

public class JsonParser extends AbstractParser {

    private final com.fasterxml.jackson.core.JsonParser jp;

    public JsonParser(com.fasterxml.jackson.core.JsonParser jp,
                      Map<String, ReadHandler> decoders,
                      DefaultReadHandler defaultDecoder,
                      MapBuilder mapBuilder,
                      ArrayBuilder arrayBuilder) {

        super(decoders, defaultDecoder, mapBuilder, arrayBuilder);
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

        if(jp.nextToken() != null)
            return parseVal(false, cache, null);
        else
            return null;
    }

    @Override
    public Object parseVal(boolean asMapKey, ReadCache cache, ReadHandler handler) throws IOException {

        switch(jp.getCurrentToken()) {
            case START_OBJECT:
                return parseTaggedMap(parseMap(asMapKey, cache, handler));
            case START_ARRAY:
                return parseArray(asMapKey, cache, handler);
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
    public Object parseMap(boolean ignored, ReadCache cache, ReadHandler handler) throws IOException {
        return parseMap(ignored, cache, handler, JsonToken.END_OBJECT);
    }

    public Object parseMap(boolean ignored, ReadCache cache, ReadHandler handler, JsonToken endToken) throws IOException {

        MapReader mr = null;

        if (handler != null) {
            mr = handler.fromMapRep();
        }

        if (mr == null) {
            mr = mapBuilder;
        }

        Object mb = mr.init();

        ReadHandler val_handler = null;

        boolean tagged = false;
        Object val = null;

        while(jp.nextToken() != endToken) {
            Object key = parseVal(true, cache, null);

            // if this is a tagged map, find the ReadHandler for the tag
            // and pass it to value parse
            if (key instanceof String) {
                String keyString = (String) key;
                if (keyString.length() > 1 && keyString.charAt(1) == Constants.TAG) {
                    tagged = true;
                    val_handler = handlers.get(keyString.substring(2));
                }
            }

            jp.nextToken();
            val = parseVal(false, cache, val_handler);

            mb = mr.add(mb, key, val);
        }

        return mr.complete(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache, ReadHandler handler) throws IOException {

        // if nextToken == JsonToken.END_ARRAY
        if (jp.nextToken() != JsonToken.END_ARRAY) {
            Object firstVal = parseVal(false, cache, null);
            if (firstVal != null && firstVal.equals(Constants.MAP_AS_ARRAY)) {
                // if the same, build a map w/ rest of array contents
                return parseMap(false, cache, null, JsonToken.END_ARRAY);
            } else {
                // else build an array starting with initial value

                ArrayReader ar = null;
                if (handler != null) {
                    ar = handler.fromArrayRep();
                }
                if (ar == null) {
                    ar = arrayBuilder;
                }

                Object ab = ar.init();

                ab = ar.add(ab, firstVal);
                while(jp.nextToken() != JsonToken.END_ARRAY) {
                    Object val = parseVal(false, cache, null);
                    ab = ar.add(ab, val);
                }
                return ar.complete(ab);
            }
        } else {
            // array is empty
            return arrayBuilder.complete(arrayBuilder.init(0));
        }
    }
}
