package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;
import com.cognitect.transit.Writer;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

public class JsonEmitter implements Emitter {

    private final static BigInteger JSON_INT_MAX = new BigInteger(String.valueOf((long) Math.pow(2, 53)));
    private final static BigInteger JSON_INT_MIN = new BigInteger("-" + JSON_INT_MAX.toString());

    private final JsonGenerator gen;
    private final Map<Class, Handler> handlers;

    public JsonEmitter(JsonGenerator gen, Map<Class, Handler> handlers) {

        this.gen = gen;
        this.handlers = handlers;
    }

    private String escape(String s) {
        if(s.length() > 0) {
            String c = s.substring(0, 1);
            if(c.equals(Writer.ESC) || c.equals(Writer.SUB) || c.equals(Writer.RESERVED)) {
                return Writer.ESC + s;
            }
            return s;
        }
        return s;
    }

    private void emitTaggedMap(Tag t, Object o, boolean ignored, WriteCache cache) throws Exception {

        emitMapStart(1L);
        emitString(Writer.ESC_TAG, t, null, true, cache);
        emit(o, false, cache);
        emitMapEnd();
    }

    private void emitEncoded(Tag t, Handler h, Object o, boolean asMapKey, WriteCache cache) throws Exception {

        if(t.allowedMapKey) {
            Object r = h.rep(o);
            if(r instanceof String) {
                emitString(Writer.ESC, t, (String)r, asMapKey, cache);
            }
            else if(asMapKey) {
                String sr = h.stringRep(o);
                if(sr instanceof String)
                    emitString(Writer.ESC, t, sr, asMapKey, cache);
                else
                    throw new Exception("Cannot be encoded as a string ");
            }
            else {
                emitTaggedMap(t, r, asMapKey, cache);
            }
        }
        else if(asMapKey)
            throw new Exception("Cannot be used as a map key ");
        else
            emitTaggedMap(t, h.rep(o), asMapKey, cache);
    }

    @Override
    public void emit(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        Handler h;
        if(o == null)
            h = handlers.get(null);
        else
            h = handlers.get(o.getClass());

        if(h != null) {
            Tag t = h.tag(o);
            if(t != null) {
                switch(t) {
                    case NULL: emitNil(asMapKey, cache); break;
                    case STRING: emitString(null, null, escape((String)h.rep(o)), asMapKey, cache); break;
                    case BOOLEAN: emitBoolean((Boolean)h.rep(o), asMapKey, cache); break;
                    case INTEGER: emitInteger(h.rep(o), asMapKey, cache); break;
                    case DOUBLE: emitDouble(h.rep(o), asMapKey, cache); break;
                    case MAP: emitMap(h.rep(o), asMapKey, cache); break;
                    case BINARY: emitBinary(h.rep(o), asMapKey, cache); break;
                    default: emitEncoded(t, h, o, asMapKey, cache); break;
                }
                gen.flush();
            }
            else {
                // TODO: how can I do this once
                throw new Exception("Not supported: " + o.getClass());
            }
        }
        else {
            throw new Exception("Not supported: " + o.getClass());
        }
    }

    public void emitMap(Object o, boolean ignored, WriteCache cache) throws Exception {

        Iterator<Map.Entry> i = ((Iterable<Map.Entry>)o).iterator();
        emitMapStart(mapSize(i));
        while(i.hasNext()) {
            Map.Entry e = i.next();
            emit(e.getKey(), true, cache);
            emit(e.getValue(), false, cache);
        }
        emitMapEnd();
    }

    @Override
    public void emitNil(boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey)
            emitString(Writer.ESC, Tag.NULL, null, asMapKey, cache);
        else
            gen.writeNull();
    }

    @Override
    public void emitString(String prefix, Tag tag, String s, boolean asMapKey, WriteCache cache) throws Exception {

        // TODO: use cache
        String outString = s;
        if(tag != null)
            outString = tag + outString;
        if(prefix != null)
            outString = prefix + outString;
        if(asMapKey)
            gen.writeFieldName(outString);
        else
            gen.writeString(outString);
    }

    @Override
    public void emitBoolean(Boolean b, boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey) {
            emitString(Writer.ESC, Tag.BOOLEAN, b.toString(), asMapKey, cache);
        }
        else {
            gen.writeBoolean(b);
        }
    }

    @Override
    public void emitInteger(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        if(o instanceof BigInteger) {
            BigInteger bi = (BigInteger)o;
            if(asMapKey || bi.compareTo(JSON_INT_MAX) > 0 || bi.compareTo(JSON_INT_MIN) < 0)
                emitString(Writer.ESC, Tag.INTEGER, bi.toString(), asMapKey, cache);
            else
                gen.writeNumber(bi.longValue());
        }
        else {
            long i;
            if(o instanceof Long)
                i = (Long)o;
            else if(o instanceof Integer)
                i = ((Integer)o).longValue();
            else if(o instanceof Short)
                i = ((Short)o).longValue();
            else if(o instanceof Byte)
                i = ((Byte)o).longValue();
            else
                throw new Exception("Unknown integer type: " + o.getClass());

            if(asMapKey || i > JSON_INT_MAX.longValue() || i < JSON_INT_MIN.longValue())
                emitString(Writer.ESC, Tag.INTEGER, String.valueOf(i), asMapKey, cache);
            else
                gen.writeNumber(i);
        }
    }

    @Override
    public void emitDouble(Object d, boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey)
            emitString(Writer.ESC, Tag.DOUBLE, d.toString(), asMapKey, cache);
        else if(d instanceof Double)
            gen.writeNumber((Double)d);
        else if(d instanceof Float)
            gen.writeNumber((Float)d);
    }

    @Override
    public void emitBinary(Object b, boolean asMapKey, WriteCache cache) throws Exception {

        byte[] encodedBytes = Base64.encodeBase64((byte[])b);
        emitString(Writer.ESC, Tag.BINARY, new String(encodedBytes), asMapKey, cache);
    }

    @Override
    public long arraySize(Object a) {
        return 0;
    }

    @Override
    public void emitArrayStart(Long size) throws Exception {

    }

    @Override
    public void emitArrayEnd() throws Exception {

    }

    @Override
    public Long mapSize(Object i) {
        return null;
    }

    @Override
    public void emitMapStart(Long ignored) throws Exception {
        gen.writeStartObject();
    }

    @Override
    public void emitMapEnd() throws Exception {
        gen.writeEndObject();
    }
}
