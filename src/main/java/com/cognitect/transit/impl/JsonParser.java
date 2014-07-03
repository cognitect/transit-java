// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

public class JsonParser extends AbstractParser {

    private final com.fasterxml.jackson.core.JsonParser jp;

    public JsonParser(com.fasterxml.jackson.core.JsonParser jp,
                      Map<String, ReadHandler> decoders,
                      DefaultReadHandler defaultDecoder,
                      MapBuilder mapBuilder, ListBuilder listBuilder,
                      ArrayBuilder arrayBuilder, SetBuilder setBuilder) {

        super(decoders, defaultDecoder, mapBuilder, listBuilder, arrayBuilder, setBuilder);
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
                return parseTaggedMap((Map) parseMap(asMapKey, cache, handler));
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

        Object mb = null;

        if (handler != null) {
            MapBuilder mapBuilder = handler.fromMapRep();
            if (mapBuilder != null) {
                mb = mapBuilder.init();
            }
        }

        if (mb == null) {
            mb = mapBuilder.init();
        }

        ReadHandler val_handler = null;

        while(jp.nextToken() != endToken) {
            Object key = parseVal(true, cache, null);

            // if this is a tagged map, find the ReadHandler for the tag
            // and pass it to value parse
            if (key instanceof String) {
                String keyString = (String) key;
                if (keyString.length() > 1 && keyString.charAt(1) == Constants.TAG) {
                    val_handler = handlers.get(keyString.substring(2));
                }
            }

            jp.nextToken();
            Object val = parseVal(false, cache, val_handler);
            mb = mapBuilder.add(mb, key, val);
        }
        return mapBuilder.map(mb);
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

                Object ab = null;

                if (handler != null) {
                    ArrayBuilder arrayBuilder = handler.fromArrayRep();
                    if (arrayBuilder != null) {
                        ab = arrayBuilder.init();
                    }
                }

                if (ab == null) {
                    ab = arrayBuilder.init();
                }

                ab = arrayBuilder.add(ab, firstVal);
                while(jp.nextToken() != JsonToken.END_ARRAY) {
                    Object val = parseVal(false, cache, null);
                    ab = arrayBuilder.add(ab, val);
                }
                return arrayBuilder.array(ab);
            }
        } else {
            // array is empty
            return arrayBuilder.array(arrayBuilder.init(0));
        }
    }
}
