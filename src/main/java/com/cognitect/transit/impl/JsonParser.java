package com.cognitect.transit.impl;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.Writer;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class JsonParser extends AbstractWriter implements Parser {

    private final com.fasterxml.jackson.core.JsonParser jp;
    private final Map<String, Decoder> decoders;

    public JsonParser(com.fasterxml.jackson.core.JsonParser jp, Map<String, Decoder> decoders) {

        this.jp = jp;
        this.decoders = decoders;
    }

    private Object decode(String dispatchKey, Object encodedVal) {

        Decoder d = decoders.get(dispatchKey);
        if(d != null) {
            return d.decode(encodedVal);
        }
        else {
            if(encodedVal instanceof String)
                return "~" + dispatchKey + encodedVal;
            else {
                System.out.println("WARNING: don't know how to decode: " + encodedVal);
                Map m = new HashMap();
                m.put("~#"+dispatchKey, encodedVal);
                return m;
            }
        }
    }

    private Object parseString(String s) {

        Object res = s;
        if(s.length() > 1) {
            if(s.charAt(0) == '~') {
                switch(s.charAt(1)) {
                    case '~': res = s.substring(1); break;
                    case '^': res = s.substring(1); break;
                    case '`': res = s.substring(1); break;
                    case '#': res = s; break;
                    default:
                        res = decode(s.substring(1, 2), s.substring(2));
                        break;
                }
            }
        }
        return res;
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

    public Object parseTaggedMap(Map m) {

        Set<Map.Entry> entrySet = m.entrySet();
        Iterator<Map.Entry> i = entrySet.iterator();
        Map.Entry entry = null;
        if(i.hasNext())
            entry = i.next();
        Object key = null;
        if(entry != null)
            key = entry.getKey();

        Object ret = m;
        if(entry != null && key instanceof String) {
            String keyString = (String)key;
            if(keyString.length() > 1 && keyString.substring(1, 2).equals(Writer.TAG)) {
                ret = decode(keyString.substring(2), entry.getValue());
            }
        }

        return ret;
    }

    @Override
    public Object parseVal(boolean asMapKey, ReadCache cache) throws IOException {

        System.out.println("parse-val: " + jp.getCurrentToken());

        switch(jp.getCurrentToken()) {
            case START_OBJECT:
                return parseTaggedMap((Map) parseMap(asMapKey, cache));
            case START_ARRAY:
                return parseArray(asMapKey, cache);
            case FIELD_NAME:
                // TODO: use cache
                return parseString(jp.getText());
            case VALUE_STRING:
                // TODO: use cache
                return parseString(jp.getText());
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
