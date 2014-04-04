package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Writer;

import java.util.Iterator;
import java.util.Map;

public abstract class AbstractEmitter implements Emitter, TagFinder {

    private final Map<Class, Handler> handlers;

    protected AbstractEmitter(Map<Class, Handler> handlers) {

        this.handlers = handlers;
    }

    protected String escape(String s) {

        if(s.length() > 0) {
            char c = s.charAt(0);
            if(c == Writer.ESC || c == Writer.SUB || c == Writer.RESERVED) {
                return Writer.ESC + s;
            }
            return s;
        }
        return s;
    }

    protected void emitTaggedMap(String t, Object o, boolean ignored, WriteCache cache) throws Exception {

        emitMapStart(1L);
        emitString(Writer.ESC_TAG, t, null, true, cache);
        emit(o, false, cache);
        emitMapEnd();
    }

    protected void emitEncoded(String t, Handler h, Object o, boolean asMapKey, WriteCache cache) throws Exception {

        if(t.length() == 1) {
            Object r = h.rep(o);
            if(r instanceof String) {
                emitString(Writer.ESC_STR, t, (String)r, asMapKey, cache);
            }
            else if(prefersStrings() || asMapKey) {
                String sr = h.stringRep(o);
                if(sr != null)
                    emitString(Writer.ESC_STR, t, sr, asMapKey, cache);
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

        Iterator<Map.Entry> i = ((Iterable<Map.Entry>)o).iterator();
        emitMapStart(mapSize(i));
        while(i.hasNext()) {
            Map.Entry e = i.next();
            emit(e.getKey(), true, cache);
            emit(e.getValue(), false, cache);
        }
        emitMapEnd();
    }

    protected void emitArray(Object o, boolean ignored, WriteCache cache) throws Exception {

        emitArrayStart(arraySize(o));
        if(o instanceof Iterable) {
            Iterator i = ((Iterable)o).iterator();
            while(i.hasNext()) {
                emit(i.next(), false, cache);
            }
        }
        else if(o instanceof int[]) {
            int[] x = (int[])o;
            for(int n : x) {
                emit(n, false, cache);
            }
        }
        else if(o instanceof long[]) {
            long[] x = (long[])o;
            for(long n : x) {
                emit(n, false, cache);
            }
        }
        else if(o instanceof float[]) {
            float[] x = (float[])o;
            for(float n : x) {
                emit(n, false, cache);
            }
        }
        else if(o instanceof boolean[]) {
            boolean[] x = (boolean[])o;
            for(boolean n : x) {
                emit(n, false, cache);
            }
        }
        else if(o instanceof double[]) {
            double[] x = (double[])o;
            for(double n : x) {
                emit(n, false, cache);
            }
        }
        else if(o instanceof char[]) {
            char[] x = (char[])o;
            for(char n : x) {
                emit(n, false, cache);
            }
        }
        else if(o instanceof short[]) {
            short[] x = (short[])o;
            for(short n : x) {
                emit(n, false, cache);
            }
        }
        emitArrayEnd();
    }

    @Override
    public String getTag(Object o) {

        Handler h;
        if(o == null)
            h = handlers.get(null);
        else
            h = handlers.get(o.getClass());

        if(h != null)
            return h.tag(o);
        else return null;
    }

    protected void marshal(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        Handler h;
        if(o == null)
            h = handlers.get(null);
        else
            h = handlers.get(o.getClass());

        boolean supported = false;
        if(h != null) {
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
}
