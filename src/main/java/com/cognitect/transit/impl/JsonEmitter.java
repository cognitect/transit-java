package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Writer;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class JsonEmitter extends AbstractEmitter {

    private final static BigInteger JSON_INT_MAX = new BigInteger(String.valueOf((long) Math.pow(2, 53)));
    private final static BigInteger JSON_INT_MIN = new BigInteger("-" + JSON_INT_MAX.toString());

    private final JsonGenerator gen;

    public JsonEmitter(JsonGenerator gen, Map<Class, Handler> handlers) {

        super(handlers);
        this.gen = gen;
    }

    @Override
    public void emit(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        marshal(o, asMapKey, cache);
    }

    @Override
    public void emitNil(boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey)
            emitString(Writer.ESC, "_", null, asMapKey, cache);
        else
            gen.writeNull();
    }

    @Override
    public void emitString(String prefix, String tag, String s, boolean asMapKey, WriteCache cache) throws Exception {

        // TODO: use cache
        StringBuilder sb = new StringBuilder();
        if(prefix != null)
            sb.append(prefix);
        if(tag != null)
            sb.append(tag);
        if(s != null)
            sb.append(s);

        if(asMapKey)
            gen.writeFieldName(sb.toString());
        else
            gen.writeString(sb.toString());
    }

    @Override
    public void emitBoolean(Boolean b, boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey) {
            emitString(Writer.ESC,"?", b.toString(), asMapKey, cache);
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
                emitString(Writer.ESC, "i", bi.toString(), asMapKey, cache);
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
                emitString(Writer.ESC, "i", String.valueOf(i), asMapKey, cache);
            else
                gen.writeNumber(i);
        }
    }

    @Override
    public void emitDouble(Object d, boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey)
            emitString(Writer.ESC, "d", d.toString(), asMapKey, cache);
        else if(d instanceof Double)
            gen.writeNumber((Double)d);
        else if(d instanceof Float)
            gen.writeNumber((Float)d);
    }

    @Override
    public void emitBinary(Object b, boolean asMapKey, WriteCache cache) throws Exception {

        byte[] encodedBytes = Base64.encodeBase64((byte[])b);
        emitString(Writer.ESC, "b", new String(encodedBytes), asMapKey, cache);
    }

    @Override
    public long arraySize(Object a) {

        if(a instanceof List)
            return ((List)a).size();
        else return 0;
    }

    @Override
    public void emitArrayStart(Long size) throws Exception {
        gen.writeStartArray();
    }

    @Override
    public void emitArrayEnd() throws Exception {
        gen.writeEndArray();
    }

    @Override
    public long mapSize(Object m) {

        if(m instanceof Map)
            return ((Map)m).size();
        else return 0;
    }

    @Override
    public void emitMapStart(Long ignored) throws Exception {
        gen.writeStartObject();
    }

    @Override
    public void emitMapEnd() throws Exception {
        gen.writeEndObject();
    }

    @Override
    public void flushWriter() throws IOException {

        gen.flush();
    }

    @Override
    public boolean prefersStrings() {

        return true;
    }
}
