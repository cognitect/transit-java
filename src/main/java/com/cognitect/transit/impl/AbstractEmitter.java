// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Writer;
import com.cognitect.transit.Handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractEmitter implements Emitter, Handler {

    private final Map<Class, Handler> handlers;

    protected AbstractEmitter(Map<Class, Handler> handlers) {

        this.handlers = handlers;
    }

    private Handler checkBaseClasses(Class c) {
        for(Class base = c.getSuperclass(); base != Object.class; base = base.getSuperclass()) {
            Handler h = handlers.get(base);
            if(h != null) {
                handlers.put(c, h);
                return h;
            }
        }
        return null;
    }

    private Handler checkBaseInterfaces(Class c) {
        Map<Class, Handler> possibles = new HashMap<Class,Handler>();
        for (Class base = c; base != Object.class; base = base.getSuperclass()) {
            for (Class itf : base.getInterfaces()) {
                Handler h = handlers.get(itf);
                if (h != null) possibles.put(itf, h);
            }
        }
        switch (possibles.size()) {
            case 0: return null;
            case 1: {
                Handler h = possibles.values().iterator().next();
                handlers.put(c, h);
                return h;
            }
            default: throw new RuntimeException("More thane one match for " + c);
        }
    }

    private Handler getHandler(Object o) {

        Class c = (o != null) ? o.getClass() : null;
        Handler h = null;

        if(h == null) {
            h = handlers.get(c);
        }
        if(h == null) {
            h = checkBaseClasses(c);
        }
        if(h == null) {
            h = checkBaseInterfaces(c);
        }

        return h;
    }

    @Override
    public String tag(Object o) {

        Handler h = getHandler(o);

        if(h != null)
            return h.tag(o);
        else return null;
    }

    @Override
    public Object rep(Object o) {

        Handler h = getHandler(o);

        if(h != null)
            return h.rep(o);
        else return null;
    }

    @Override
    public String stringRep(Object o) {

        Handler h = getHandler(o);

        if(h != null)
            return h.stringRep(o);
        else return null;
    }

    protected String escape(String s) {

        if(s.length() > 0) {
            char c = s.charAt(0);
            if(c == Constants.RESERVED) {
                return Constants.ESC + s.substring(1);
            }
            if(c == Constants.ESC || c == Constants.SUB) {
                return Constants.ESC + s;
            }
        }
        return s;
    }

    protected void emitTaggedMap(String t, Object o, boolean ignored, WriteCache cache) throws Exception {

        emitMapStart(1L);
        emitString(Constants.ESC_TAG, t, null, true, cache);
        marshal(o, false, cache);
        emitMapEnd();
    }

    protected void emitEncoded(String t, Handler h, Object o, boolean asMapKey, WriteCache cache) throws Exception {

        if(t.length() == 1) {
            Object r = h.rep(o);
            if(r instanceof String) {
                emitString(Constants.ESC_STR, t, (String)r, asMapKey, cache);
            }
            else if(prefersStrings() || asMapKey) {
                String sr = h.stringRep(o);
                if(sr != null)
                    emitString(Constants.ESC_STR, t, sr, asMapKey, cache);
                else
                    throw new Exception("Cannot be encoded as a string " + o);
            }
            else {
                emitTaggedMap(t, r, asMapKey, cache);
            }
        }
        else if(asMapKey)
            throw new Exception("Cannot be used as a map key " + o);
        else
            emitTaggedMap(t, h.rep(o), asMapKey, cache);
    }

    protected void emitMap(Object o, boolean ignored, WriteCache cache) throws Exception {

        Iterable<Map.Entry> i = ((Iterable<Map.Entry>)o);
        emitMapStart(mapSize(i));
        for (Map.Entry e : i) {
            marshal(e.getKey(), true, cache);
            marshal(e.getValue(), false, cache);
        }
        emitMapEnd();
    }

    protected void emitArray(Object o, boolean ignored, WriteCache cache) throws Exception {

        emitArrayStart(arraySize(o));
        if(o instanceof Iterable) {
            Iterator i = ((Iterable)o).iterator();
            while(i.hasNext()) {
                marshal(i.next(), false, cache);
            }
        }
        else if (o instanceof Object[]) {
            for(Object x : (Object[]) o) {
                marshal(x, false, cache);
            }
        }
        else if(o instanceof int[]) {
            int[] x = (int[])o;
            for(int n : x) {
                marshal(n, false, cache);
            }
        }
        else if(o instanceof long[]) {
            long[] x = (long[])o;
            for(long n : x) {
                marshal(n, false, cache);
            }
        }
        else if(o instanceof float[]) {
            float[] x = (float[])o;
            for(float n : x) {
                marshal(n, false, cache);
            }
        }
        else if(o instanceof boolean[]) {
            boolean[] x = (boolean[])o;
            for(boolean n : x) {
                marshal(n, false, cache);
            }
        }
        else if(o instanceof double[]) {
            double[] x = (double[])o;
            for(double n : x) {
                marshal(n, false, cache);
            }
        }
        else if(o instanceof char[]) {
            char[] x = (char[])o;
            for(char n : x) {
                marshal(n, false, cache);
            }
        }
        else if(o instanceof short[]) {
            short[] x = (short[])o;
            for(short n : x) {
                marshal(n, false, cache);
            }
        }
        emitArrayEnd();
    }

    protected void marshal(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        Handler h = getHandler(o);

        boolean supported = false;
        if(h != null) { // TODO: maybe remove getHandler call and this check and just call tag
            String t = h.tag(o);
            if(t != null) {
                supported = true;
                if(t.length() == 1) {
                    switch(t.charAt(0)) {
                        case '_': emitNil(asMapKey, cache); break;
                        case 's': emitString(null, null, escape((String)h.rep(o)), asMapKey, cache); break;
                        case '?': emitBoolean((Boolean)h.rep(o), asMapKey, cache); break;
                        case 'i': emitInteger(h.rep(o), asMapKey, cache); break;
                        case 'd': emitDouble(h.rep(o), asMapKey, cache); break;
                        case 'b': emitBinary(h.rep(o), asMapKey, cache); break;
                        case '\'': emitQuoted(h.rep(o), cache); break;
                        default: emitEncoded(t, h, o, asMapKey, cache); break;
                    }
                }
                else {
                    if(t.equals("array"))
                        emitArray(h.rep(o), asMapKey, cache);
                    else if(t.equals("map"))
                        emitMap(h.rep(o), asMapKey, cache);
                    else
                        emitEncoded(t, h, o, asMapKey, cache);
                }
                flushWriter();
            }
        }

        if(!supported)
            throw new Exception("Not supported: " + o.getClass());
    }

    protected void marshalTop(Object o, WriteCache cache) throws Exception {

        String tag = tag(o);
        if(tag != null) {

            if(tag.length() == 1)
                o = new Quote(o);

            marshal(o, false, cache);
        }
        else {
            throw new Exception("Not supported: " + o);
        }
    }
}
