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
            mapBuilder.add(mb, k, v);
        }
        return mapBuilder.map(mb);
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache) throws IOException {

        Object ab = arrayBuilder.init();
        while(jp.nextToken() != JsonToken.END_ARRAY) {
            arrayBuilder.add(ab, parseVal(false, cache));
        }
        return arrayBuilder.array(ab);
    }
}
