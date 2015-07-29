// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;

import java.util.*;

public abstract class AbstractEmitter implements Emitter
{
    private WriteHandlerMap writeHandlerMap;

    protected AbstractEmitter(WriteHandlerMap writeHandlerMap) {
        this.writeHandlerMap = writeHandlerMap;
    }

    protected String escape(String s) {

        int l = s.length();
        if(l > 0) {
            char c = s.charAt(0);
            if(c == Constants.ESC || c == Constants.SUB || c == Constants.RESERVED) {
                return Constants.ESC + s;
            }
        }
        return s;
    }

    protected void emitTagged(String t, Object o, boolean ignored, WriteCache cache) throws Exception {

        emitArrayStart(2L);
        emitString(Constants.ESC_TAG, t, "", false, cache);
        marshal(o, false, cache);
        emitArrayEnd();
    }

    protected void emitEncoded(String t, WriteHandler<Object, Object> h, Object o, boolean asMapKey, WriteCache cache) throws Exception {

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
                emitTagged(t, r, asMapKey, cache);
            }
        }
        else if(asMapKey)
            throw new Exception("Cannot be used as a map key " + o);
        else
            emitTagged(t, h.rep(o), asMapKey, cache);
    }

    protected void emitMap(Object m, boolean ignored, WriteCache cache) throws Exception
    {
        Iterable<Map.Entry<Object, Object>> entries;
        if (m instanceof Map)
            entries = ((Map<Object, Object>) m).entrySet();
        else {
            entries = (Iterable<Map.Entry<Object, Object>>) m;
        }
        emitMap(entries, ignored, cache);
    }

    abstract protected void emitMap(Iterable<Map.Entry<Object, Object>> i, boolean ignored, WriteCache cache) throws Exception;

    protected void emitArray(Object o, boolean ignored, WriteCache cache) throws Exception {

        emitArrayStart(Util.arraySize(o));

	    if(o instanceof RandomAccess){
	        List xs = (List)o;
	        for(int i=0;i<xs.size();i++)
		        marshal(xs.get(i), false, cache);
	    }
        else if(o instanceof Iterable) {
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
                emitInteger(n, false, cache);
            }
        }
        else if(o instanceof long[]) {
            long[] x = (long[])o;
            for(long n : x) {
                emitInteger(n, false, cache);
            }
        }
        else if(o instanceof float[]) {
            float[] x = (float[])o;
            for(float n : x) {
                emitDouble(n, false, cache);
            }
        }
        else if(o instanceof boolean[]) {
            boolean[] x = (boolean[])o;
            for(boolean n : x) {
                emitBoolean(n, false, cache);
            }
        }
        else if(o instanceof double[]) {
            double[] x = (double[])o;
            for(double n : x) {
                emitDouble(n, false, cache);
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
                emitInteger(n, false, cache);
            }
        }
        emitArrayEnd();
    }

    @SuppressWarnings("unchecked")
    protected void marshal(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        WriteHandler<Object, Object> h = writeHandlerMap.getHandler(o);

        boolean supported = false;
        if(h != null) { // TODO: maybe remove getWriteHandler call and this check and just call tag
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
                        case '\'': emitTagged(t, h.rep(o), false, cache); break;
                        default: emitEncoded(t, h, o, asMapKey, cache); break;
                    }
                }
                else {
                    if(t.equals("array"))
                        emitArray(h.rep(o), asMapKey, cache);
                    else if(t.equals("map")) {
                        emitMap(h.rep(o), asMapKey, cache);
                    }
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

        WriteHandler<Object, Object> h = writeHandlerMap.getHandler(o);
        if (h == null) {
            throw new Exception("Not supported: " + o);
        }
        String tag = h.tag(o);
        if (tag == null) {
            throw new Exception("Not supported: " + o);
        }

        if (tag.length() == 1)
            o = new Quote(o);

        marshal(o, false, cache);
    }
}
