// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractParser implements Parser {

    public static final SimpleDateFormat dateTimeFormat;
    static {
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateTimeFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
    }

    protected final Map<String, ReadHandler> handlers;
    private final DefaultReadHandler defaultHandler;
    protected MapBuilder mapBuilder;
    protected ArrayBuilder arrayBuilder;

    protected AbstractParser(Map<String, ReadHandler> handlers,
                             DefaultReadHandler defaultHandler,
                             MapBuilder mapBuilder,
                             ArrayBuilder arrayBuilder) {
        this.handlers = handlers;
        this.defaultHandler = defaultHandler;
        this.mapBuilder = mapBuilder;
        this.arrayBuilder = arrayBuilder;
    }

    protected Object decode(String tag, Object rep) {

        ReadHandler d = handlers.get(tag);
        if(d != null) {
            return d.fromRep(rep);
        } else if(defaultHandler != null) {
            return defaultHandler.fromRep(tag, rep);
        } else {
            throw new RuntimeException("Cannot fromRep " + tag + ": " + rep.toString());
        }
    }

    protected Object parseString(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            if (s.length() > 1) {
                if (s.charAt(0) == Constants.ESC) {
                    switch (s.charAt(1)) {
                        case Constants.ESC:
                        case Constants.SUB:
                        case Constants.RESERVED:
                            return s.substring(1);
                        case Constants.TAG:
                            return s;
                        default:
                            return decode(s.substring(1, 2), s.substring(2));
                    }
                }
            }
        }
        return o;
    }

    protected Object parseTaggedMap(Object o) {
        if (o instanceof Map) {
            Map m = (Map) o;
            if (m.size() != 1)
                return m;
            Set<Map.Entry> entrySet = m.entrySet();
            Iterator<Map.Entry> i = entrySet.iterator();
            Map.Entry entry = i.next();
            Object key = entry.getKey();

            if (key instanceof String) {
                String keyString = (String) key;
                if (keyString.length() > 1 && keyString.charAt(1) == Constants.TAG) {
                    return decode(keyString.substring(2), entry.getValue());
                }
            }
            return m;
        }
        return o;
    }
}
