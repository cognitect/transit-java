// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Writer;
import com.cognitect.transit.Decoder;

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

    protected AbstractParser(Map<String, Decoder> decoders) {

        this.decoders = decoders;
    }

    protected Object decode(String tag, Object rep) {

        Decoder d = decoders.get(tag);
        if(d != null) {
            return d.decode(rep);
        }
        else {
            if(tag.length() == 1 && rep instanceof String)
                return Writer.RESERVED + tag + rep;
            else
                return new TaggedValue(tag, rep);
        }
    }

    protected Object parseString(String s) {

        Object res = s;
        if(s.length() > 1) {
            if(s.charAt(0) == Writer.ESC) {
                switch(s.charAt(1)) {
                    case Writer.ESC: res = s.substring(1); break;
                    case Writer.SUB: res = s.substring(1); break;
                    case Writer.RESERVED: res = s.substring(1); break;
                    case Writer.TAG: res = s; break;
                    default:
                        res = decode(s.substring(1, 2), s.substring(2));
                        break;
                }
            }
        }
        return res;
    }

    protected Object parseTaggedMap(Map m) {

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
            if(keyString.length() > 1 && keyString.charAt(1) == Writer.TAG) {
                ret = decode(keyString.substring(2), entry.getValue());
            }
        }

        return ret;
    }
}
