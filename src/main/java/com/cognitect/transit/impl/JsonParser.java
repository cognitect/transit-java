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
                      Map<String, Decoder> decoders,
                      MapBuilder mapBuilder, ListBuilder listBuilder,
                      ArrayBuilder arrayBuilder, SetBuilder setBuilder) {

        super(decoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);
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
            return parseVal(false, cache);
        else
            return null;
    }

    @Override
    public Object parseVal(boolean asMapKey, ReadCache cache) throws IOException {

        switch(jp.getCurrentToken()) {
            case START_OBJECT:
                return parseTaggedMap((Map) parseMap(asMapKey, cache));
            case START_ARRAY:
                return parseArray(asMapKey, cache);
            case FIELD_NAME:
                return parseString(cache.cacheRead(jp.getText(), asMapKey));
            case VALUE_STRING:
                return parseString(cache.cacheRead(jp.getText(), asMapKey));
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
    public Object parseMap(boolean ignored, ReadCache cache) throws IOException {
        return parseMap(ignored, cache, JsonToken.END_OBJECT);
    }

    public Object parseMap(boolean ignored, ReadCache cache, JsonToken endToken) throws IOException {

        Object mb = mapBuilder.init();
        while(jp.nextToken() != endToken) {
            Object k = parseVal(true, cache);
            jp.nextToken();
            Object v = parseVal(false, cache);
            mb = mapBuilder.add(mb, k, v);
        }
        return mapBuilder.map(mb);
    }

    private Object buildMap(java.util.List contents) throws IOException {
        int elemCount = (contents.size() - 1) / 2;
        Object mb = mapBuilder.init(elemCount);

        for(int i = 1; i < elemCount * 2; i += 2) {
            mb = mapBuilder.add(mb, contents.get(i), contents.get(i+1));
        }

        return mapBuilder.map(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache) throws IOException {

        // if nextToken == JsonToken.END_ARRAY
        if (jp.nextToken() != JsonToken.END_ARRAY) {
            Object firstVal = parseVal(false, cache);
            if (firstVal.equals(Constants.MACHINE_MAP_STR)) {
                // if the same, build a map w/ rest of array contents
                return parseMap(false, cache, JsonToken.END_ARRAY);
            } else {
                // else build an array starting with initial value
                Object ab = arrayBuilder.init();
                ab = arrayBuilder.add(ab, firstVal);
                while(jp.nextToken() != JsonToken.END_ARRAY) {
                    Object val = parseVal(false, cache);
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
