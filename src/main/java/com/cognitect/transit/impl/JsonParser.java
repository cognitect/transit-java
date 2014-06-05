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

        Object mb = mapBuilder.init();
        while(jp.nextToken() != JsonToken.END_OBJECT) {
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

        Object ab = arrayBuilder.init();
        while(jp.nextToken() != JsonToken.END_ARRAY) {
            Object val = parseVal(false, cache);
            ab = arrayBuilder.add(ab, val);
        }

        if (arrayBuilder.size(ab) > 0 && arrayBuilder.getAt(ab, 0).equals(Constants.MACHINE_MAP_STR)) {
            return buildMap(arrayBuilder.array(ab));
        }

        return arrayBuilder.array(ab);
    }
}
