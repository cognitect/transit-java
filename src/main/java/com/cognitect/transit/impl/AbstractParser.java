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

    private final Map<String, Decoder> decoders;
    protected MapBuilder mapBuilder;
    protected ListBuilder listBuilder;
    protected SetBuilder setBuilder;
    protected ArrayBuilder arrayBuilder;

    protected AbstractParser(Map<String, Decoder> decoders,
                             MapBuilder mapBuilder, ListBuilder listBuilder,
                             ArrayBuilder arrayBuilder, SetBuilder setBuilder) {

        this.decoders = decoders;
        this.mapBuilder = mapBuilder;
        this.listBuilder = listBuilder;
        this.arrayBuilder = arrayBuilder;
        this.setBuilder = setBuilder;
    }

    protected Object decode(String tag, Object rep) {

        Decoder d = decoders.get(tag);
        if(d != null) {
            return d.decode(rep);
        } else {
            return TransitFactory.taggedValue(tag, rep);
        }
    }

    protected Object parseString(String s) {

        Object res = s;
        if(s.length() > 1) {
            if(s.charAt(0) == Constants.ESC) {
                switch(s.charAt(1)) {
                    case Constants.ESC:
                    case Constants.SUB:
                    case Constants.RESERVED: res = s.substring(1); break;
                    case Constants.TAG: res = s; break;
                    default:
                        res = decode(s.substring(1, 2), s.substring(2));
                        break;
                }
            }
        }
        return res;
    }

    protected Object parseTaggedMap(Map m) {
	    if(m.size() != 1)
		    return m;
        Set<Map.Entry> entrySet = m.entrySet();
        Iterator<Map.Entry> i = entrySet.iterator();
        Map.Entry entry = i.next();
        Object key = entry.getKey();

        if(key instanceof String) {
            String keyString = (String)key;
            if(keyString.length() > 1 && keyString.charAt(1) == Constants.TAG) {
                return decode(keyString.substring(2), entry.getValue());
            }
        }
        return m;
    }
}
