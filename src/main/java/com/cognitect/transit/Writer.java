// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import com.cognitect.transit.impl.*;
import com.cognitect.transit.impl.handler.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;

public class Writer {

    public static final char ESC = '~';
    public static final String ESC_STR = String.valueOf(ESC);
    public static final char TAG = '#';
    public static final String TAG_STR = String.valueOf(TAG);
    public static final char SUB = '^';
    public static final String SUB_STR = String.valueOf(SUB);
    public static final char RESERVED = '`';
    public static final String ESC_TAG = String.valueOf(ESC) + TAG;

    public static enum Format { JSON, MSGPACK }

    public static IWriter instance(Format type, OutputStream in, Map<Class, Handler> customHandlers) throws IOException, IllegalArgumentException {
        switch (type) {
            case JSON:    return getJsonInstance(in, customHandlers);
            case MSGPACK: return getMsgpackInstance(in, customHandlers);
            default: throw new IllegalArgumentException("Unknown Reader type: " + type.toString());
        }
    }

    public static Map<Class, Handler> defaultHandlers() {

        Map<Class, Handler> handlers = new HashMap<Class, Handler>();

        Handler integerHandler = new NumberHandler("i");
        Handler doubleHandler = new NumberHandler("d");

        handlers.put(Boolean.class, new BooleanHandler());
        handlers.put(null, new NullHandler());
        handlers.put(String.class, new ToStringHandler("s"));
        handlers.put(Integer.class, integerHandler);
        handlers.put(Long.class, integerHandler);
        handlers.put(Short.class, integerHandler);
        handlers.put(Byte.class, integerHandler);
        handlers.put(BigInteger.class, integerHandler);
        handlers.put(Float.class, doubleHandler);
        handlers.put(Double.class, doubleHandler);
        handlers.put(Map.class, new MapHandler());
        handlers.put(BigDecimal.class, new ToStringHandler("f"));
        handlers.put(Character.class, new ToStringHandler("c"));
        handlers.put(Keyword.class, new ToStringHandler(":"));
        handlers.put(Symbol.class, new ToStringHandler("$"));
        handlers.put(byte[].class, new BinaryHandler());
        handlers.put(UUID.class, new UUIDHandler());
        handlers.put(URI.class, new ToStringHandler("r"));
        handlers.put(URI.class, new ToStringHandler("r"));
        handlers.put(List.class, new ListHandler());
        handlers.put(Object[].class, new ArrayHandler("array"));
        handlers.put(int[].class, new ArrayHandler("ints"));
        handlers.put(long[].class, new ArrayHandler("longs"));
        handlers.put(float[].class, new ArrayHandler("floats"));
        handlers.put(double[].class, new ArrayHandler("doubles"));
        handlers.put(short[].class, new ArrayHandler("shorts"));
        handlers.put(boolean[].class, new ArrayHandler("bools"));
        handlers.put(char[].class, new ArrayHandler("chars"));
        handlers.put(Set.class, new SetHandler());
        handlers.put(Date.class, new TimeHandler());
        handlers.put(Ratio.class, new RatioHandler());
        handlers.put(Quote.class, new QuoteHandler());
        handlers.put(TaggedValue.class, new TaggedValueHandler());
        handlers.put(Object.class, new ObjectHandler());

        return handlers;
    }

    static IWriter getJsonInstance(final OutputStream out, Map<Class, Handler> customHandlers) throws IOException {

            JsonFactory jf = new JsonFactory();
            JsonGenerator gen = jf.createGenerator(out);

            Map<Class, Handler> handlers = defaultHandlers();
            if(customHandlers != null) {
                Iterator<Map.Entry<Class, Handler>> i = customHandlers.entrySet().iterator();
                while(i.hasNext()) {
                    Map.Entry<Class, Handler> e = i.next();
                    handlers.put(e.getKey(), e.getValue());
                }
            }

            final JsonEmitter emitter = new JsonEmitter(gen, handlers);

            Iterator<Handler> i = handlers.values().iterator();
            while(i.hasNext()) {
                Handler h = i.next();
                if(h instanceof HandlerAware)
                    ((HandlerAware)h).setHandler(emitter);
            }

            return new IWriter() {
                @Override
                public synchronized void write(Object o) throws Exception {

                    emitter.emit(o, false, new WriteCache());
                    out.flush();
                }
            };
    }

    static IWriter getMsgpackInstance(final OutputStream out, Map<Class, Handler> customHandlers) throws IOException {

        MessagePack mp = new MessagePack();
        Packer p = mp.createPacker(out);

        Map<Class, Handler> handlers = defaultHandlers();
        if(customHandlers != null) {
            Iterator<Map.Entry<Class, Handler>> i = customHandlers.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<Class, Handler> e = i.next();
                handlers.put(e.getKey(), e.getValue());
            }
        }

        final MsgpackEmitter emitter = new MsgpackEmitter(p, handlers);

        Iterator<Handler> i = handlers.values().iterator();
        while(i.hasNext()) {
            Handler h = i.next();
            if(h instanceof HandlerAware)
                ((HandlerAware)h).setHandler(emitter);
        }

        return new IWriter() {
            @Override
            public synchronized void write(Object o) throws Exception {

                emitter.emit(o, false, new WriteCache());
                out.flush();
            }
        };
    }
}
