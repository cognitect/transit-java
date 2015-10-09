// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;
import com.cognitect.transit.DefaultReadHandler;
import com.cognitect.transit.MapReader;
import com.cognitect.transit.ReadHandler;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public abstract class AbstractParser implements Parser {

    private static final ThreadLocal<SimpleDateFormat> dateTimeFormat =
        new ThreadLocal<SimpleDateFormat>() {
            @Override protected SimpleDateFormat initialValue() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                return sdf;
            }
        };

    public static SimpleDateFormat getDateTimeFormat() {
        return dateTimeFormat.get();
    }

    protected final Map<String, ReadHandler<?,?>> handlers;
    private final DefaultReadHandler<?> defaultHandler;
    protected MapReader<Object, Map<Object, Object>, Object, Object> mapBuilder;
    protected ArrayReader<Object, List<Object>, Object> listBuilder;

    @SuppressWarnings("unchecked")
    protected AbstractParser(Map<String, ReadHandler<?,?>> handlers,
                             DefaultReadHandler<?> defaultHandler,
                             MapReader<?, Map<Object, Object>, Object, Object> mapBuilder,
                             ArrayReader<?, List<Object>, Object> listBuilder) {
        this.handlers = handlers;
        this.defaultHandler = defaultHandler;
        this.mapBuilder = (MapReader<Object, Map<Object, Object>, Object, Object>) mapBuilder;
        this.listBuilder = (ArrayReader<Object, List<Object>, Object>) listBuilder;
    }

    @SuppressWarnings("unchecked")
    protected ReadHandler<Object, Object> getHandler(String tag) {
        return (ReadHandler<Object, Object>) handlers.get(tag);
    }

    protected Object decode(String tag, Object rep) {

        ReadHandler<Object, Object> d = getHandler(tag);
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
                switch(s.charAt(0)) {
                    case Constants.ESC: {
                        switch (s.charAt(1)) {
                            case Constants.ESC:
                            case Constants.SUB:
                            case Constants.RESERVED:
                                return s.substring(1);
                            case Constants.TAG:
                                return new Tag(s.substring(2));
                            default:
                                return decode(s.substring(1, 2), s.substring(2));
                        }
                    }
                    case Constants.SUB: {
                        if (s.charAt(1) == ' ') {
                            return Constants.MAP_AS_ARRAY;
                        }
                    }
                }
            }
        }
        return o;
    }
}
