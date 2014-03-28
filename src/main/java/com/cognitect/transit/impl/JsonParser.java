package com.cognitect.transit.impl;

import com.cognitect.transit.Decoder;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class JsonParser extends AbstractParser {

    private final com.fasterxml.jackson.core.JsonParser jp;

    public JsonParser(com.fasterxml.jackson.core.JsonParser jp, Map<String, Decoder> decoders) {

        super(decoders);
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

        Map map = new HashMap();
        while(jp.nextToken() != JsonToken.END_OBJECT) {
            Object k = parseVal(true, cache);
            jp.nextToken();
            Object v = parseVal(false, cache);
            map.put(k, v);
        }

        return map;
    }

    @Override
    public Object parseArray(boolean ignored, ReadCache cache) throws IOException {

        List list = new ArrayList();
        while(jp.nextToken() != JsonToken.END_ARRAY) {
            list.add(parseVal(false, cache));
        }
        return list;
    }
}
