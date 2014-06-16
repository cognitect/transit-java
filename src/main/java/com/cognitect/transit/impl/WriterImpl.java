// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
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

public class WriterImpl {

    public static Map<Class, Handler> defaultHandlers() {

        Map<Class, Handler> handlers = new HashMap<Class, Handler>();

        Handler integerHandler = new Handlers.NumberHandler("i");
        Handler doubleHandler = new Handlers.NumberHandler("d");

        handlers.put(Boolean.class, new Handlers.BooleanHandler());
        handlers.put(null, new Handlers.NullHandler());
        handlers.put(String.class, new Handlers.ToStringHandler("s"));
        handlers.put(Integer.class, integerHandler);
        handlers.put(Long.class, integerHandler);
        handlers.put(Short.class, integerHandler);
        handlers.put(Byte.class, integerHandler);
        handlers.put(BigInteger.class, integerHandler);
        handlers.put(Float.class, doubleHandler);
        handlers.put(Double.class, doubleHandler);
        handlers.put(Map.class, new Handlers.MapHandler());
        handlers.put(BigDecimal.class, new Handlers.ToStringHandler("f"));
        handlers.put(Character.class, new Handlers.ToStringHandler("c"));
        handlers.put(Keyword.class, new Handlers.ToStringHandler(":"));
        handlers.put(Symbol.class, new Handlers.ToStringHandler("$"));
        handlers.put(byte[].class, new Handlers.BinaryHandler());
        handlers.put(UUID.class, new Handlers.UUIDHandler());
        handlers.put(URI.class, new Handlers.ToStringHandler("r"));
        handlers.put(URI.class, new Handlers.ToStringHandler("r"));
        handlers.put(List.class, new Handlers.ListHandler());
        handlers.put(Object[].class, new Handlers.ArrayHandler("array"));
        handlers.put(int[].class, new Handlers.ArrayHandler("ints"));
        handlers.put(long[].class, new Handlers.ArrayHandler("longs"));
        handlers.put(float[].class, new Handlers.ArrayHandler("floats"));
        handlers.put(double[].class, new Handlers.ArrayHandler("doubles"));
        handlers.put(short[].class, new Handlers.ArrayHandler("shorts"));
        handlers.put(boolean[].class, new Handlers.ArrayHandler("bools"));
        handlers.put(char[].class, new Handlers.ArrayHandler("chars"));
        handlers.put(Set.class, new Handlers.SetHandler());
        handlers.put(Date.class, new Handlers.TimeHandler());
        handlers.put(Ratio.class, new Handlers.RatioHandler());
        handlers.put(Quote.class, new Handlers.QuoteHandler());
        handlers.put(TaggedValue.class, new Handlers.TaggedValueHandler());
        handlers.put(Object.class, new Handlers.ObjectHandler());

        return handlers;
    }

    private static Map<Class, Handler> handlers(Map<Class, Handler> customHandlers) {
        Map<Class, Handler> handlers = defaultHandlers();
        if(customHandlers != null) {
            Iterator<Map.Entry<Class, Handler>> i = customHandlers.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<Class, Handler> e = i.next();
                handlers.put(e.getKey(), e.getValue());
            }
        }
        return handlers;
    }

    private static void setSubHandler(Map<Class, Handler> handlers, Handler subHandler) {
        Iterator<Handler> i = handlers.values().iterator();
        while(i.hasNext()) {
            Handler h = i.next();
            if(h instanceof HandlerAware)
                ((HandlerAware)h).setHandler(subHandler);
        }
    }

    public static Writer getJsonInstance(final OutputStream out, Map<Class, Handler> customHandlers, boolean verboseMode) throws IOException {

        JsonFactory jf = new JsonFactory();
        JsonGenerator gen = jf.createGenerator(out);

        Map<Class, Handler> handlers = handlers(customHandlers);

        final JsonEmitter emitter;
        if (verboseMode)
            emitter = new JsonVerboseEmitter(gen, handlers);
        else
            emitter = new JsonEmitter(gen, handlers);

        setSubHandler(handlers, emitter);

        final WriteCache wc = new WriteCache(!verboseMode);

        return new Writer() {
            @Override
            public void write(Object o) throws Exception {
                emitter.emit(o, false, wc.init());
                out.flush();
            }
        };
    }

    public static Writer getMsgpackInstance(final OutputStream out, Map<Class, Handler> customHandlers) throws IOException {

        MessagePack mp = new MessagePack();
        Packer p = mp.createPacker(out);

        Map<Class, Handler> handlers = handlers(customHandlers);

        final MsgpackEmitter emitter = new MsgpackEmitter(p, handlers);

        setSubHandler(handlers, emitter);

	    final WriteCache wc = new WriteCache(true);

        return new Writer() {
            @Override
            public void write(Object o) throws Exception {
                emitter.emit(o, false, wc.init());
                out.flush();
            }
        };
    }
}
