package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Keyword;
import com.cognitect.transit.Symbol;
import com.cognitect.transit.Writer;
import com.cognitect.transit.impl.handler.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import javassist.bytecode.ByteArray;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonWriter extends AbstractWriter implements Writer {

    private final OutputStream out;
    private final Emitter e;

    private final Map<Class, Handler> handlers = new HashMap<Class, Handler>();

    public JsonWriter(OutputStream out) throws IOException {

        this.out = out;
        JsonFactory jf = new JsonFactory();
        JsonGenerator gen = jf.createGenerator(out);

        handlers.put(Boolean.class, new BooleanHandler());
        handlers.put(null, new NullHandler());
        handlers.put(String.class, new StringHandler());
        handlers.put(Integer.class, new IntegerHandler());
        handlers.put(Long.class, new IntegerHandler());
        handlers.put(Short.class, new IntegerHandler());
        handlers.put(Byte.class, new IntegerHandler());
        handlers.put(BigInteger.class, new IntegerHandler());
        handlers.put(Float.class, new DoubleHandler());
        handlers.put(Double.class, new DoubleHandler());
        handlers.put(HashMap.class, new MapHandler());
        handlers.put(BigDecimal.class, new BigDecimalHandler());
        handlers.put(Character.class, new CharacterHandler());
        handlers.put(Keyword.class, new KeywordHandler());
        handlers.put(Symbol.class, new SymbolHandler());
        handlers.put(byte[].class, new BinaryHandler());
        handlers.put(UUID.class, new UUIDHandler());
        handlers.put(URI.class, new URIHandler());

        e = new JsonEmitter(gen, handlers);
    }

    public JsonWriter setCustomHandler(Class c, Handler handler) {

        handlers.put(c, handler);
        return this;
    }

    @Override
    public void write(Object o) throws Exception {

        e.emit(o, false, null);
    }
}
