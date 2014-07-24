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
import java.util.*;

public class WriterFactory {

    public static Map<Class, WriteHandler<?,?>> defaultHandlers() {

        Map<Class, WriteHandler<?,?>> handlers = new HashMap<Class, WriteHandler<?,?>>();

        WriteHandler integerHandler = new WriteHandlers.NumberWriteHandler("i");
        WriteHandler doubleHandler = new WriteHandlers.NumberWriteHandler("d");
        WriteHandler uriHandler = new WriteHandlers.ToStringWriteHandler("r");
        WriteHandler arrayHandler = new WriteHandlers.ArrayWriteHandler();

        handlers.put(Boolean.class, new WriteHandlers.BooleanWriteHandler());
        handlers.put(null, new WriteHandlers.NullWriteHandler());
        handlers.put(String.class, new WriteHandlers.ToStringWriteHandler("s"));
        handlers.put(Integer.class, integerHandler);
        handlers.put(Long.class, integerHandler);
        handlers.put(Short.class, integerHandler);
        handlers.put(Byte.class, integerHandler);
        handlers.put(BigInteger.class, new WriteHandlers.ToStringWriteHandler("n"));
        handlers.put(Float.class, doubleHandler);
        handlers.put(Double.class, doubleHandler);
        handlers.put(Map.class, new WriteHandlers.MapWriteHandler());
        handlers.put(BigDecimal.class, new WriteHandlers.ToStringWriteHandler("f"));
        handlers.put(Character.class, new WriteHandlers.ToStringWriteHandler("c"));
        handlers.put(Keyword.class, new WriteHandlers.ToStringWriteHandler(":"));
        handlers.put(Symbol.class, new WriteHandlers.ToStringWriteHandler("$"));
        handlers.put(byte[].class, new WriteHandlers.BinaryWriteHandler());
        handlers.put(UUID.class, new WriteHandlers.UUIDWriteHandler());
        handlers.put(java.net.URI.class, uriHandler);
        handlers.put(com.cognitect.transit.URI.class, uriHandler);
        handlers.put(List.class, new WriteHandlers.ListWriteHandler());
        handlers.put(Object[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(int[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(long[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(float[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(double[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(short[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(boolean[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(char[].class, new WriteHandlers.ArrayWriteHandler());
        handlers.put(Set.class, new WriteHandlers.SetWriteHandler());
        handlers.put(Date.class, new WriteHandlers.TimeWriteHandler());
        handlers.put(Ratio.class, new WriteHandlers.RatioWriteHandler());
        handlers.put(Link.class, new WriteHandlers.LinkWriteHandler());
        handlers.put(Quote.class, new WriteHandlers.QuoteAbstractEmitter());
        handlers.put(TaggedValue.class, new WriteHandlers.TaggedValueWriteHandler());
        handlers.put(Object.class, new WriteHandlers.ObjectWriteHandler());

        return handlers;
    }

    private static Map<Class, WriteHandler<?,?>> handlers(Map<Class, WriteHandler<?,?>> customHandlers) {
        Map<Class, WriteHandler<?,?>> handlers = defaultHandlers();
        if (customHandlers != null) {
            handlers.putAll(customHandlers);
        }
        return handlers;
    }

    private static void setSubHandler(Map<Class, WriteHandler<?,?>> handlers, AbstractEmitter abstractEmitter) {
        Iterator<WriteHandler<?,?>> i = handlers.values().iterator();
        while(i.hasNext()) {
            WriteHandler h = i.next();
            if(h instanceof AbstractEmitterAware)
                ((AbstractEmitterAware)h).setEmitter(abstractEmitter);
        }
    }

    private static Map<Class, WriteHandler<?,?>> getVerboseHandlers(Map<Class, WriteHandler<?,?>> handlers) {
        Map<Class, WriteHandler<?,?>> verboseHandlers = new HashMap(handlers.size());
        for(Map.Entry<Class, WriteHandler<?,?>> entry : handlers.entrySet()) {
            WriteHandler verboseHandler = entry.getValue().getVerboseHandler();
            verboseHandlers.put(
                    entry.getKey(),
                    (verboseHandler == null) ? entry.getValue() : verboseHandler);
        }
        return verboseHandlers;
    }

    public static Writer getJsonInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers, boolean verboseMode) throws IOException {

        JsonFactory jf = new JsonFactory();
        JsonGenerator gen = jf.createGenerator(out);

        Map<Class, WriteHandler<?,?>> handlers = handlers(customHandlers);

        final JsonEmitter emitter;
        if (verboseMode) {
            emitter = new JsonVerboseEmitter(gen, getVerboseHandlers(handlers));
        } else {
            emitter = new JsonEmitter(gen, handlers);
        }

        setSubHandler(handlers, emitter);

        final WriteCache wc = new WriteCache(!verboseMode);

        return new Writer() {
            @Override
            public void write(Object o) {
                try {
                    emitter.emit(o, false, wc.init());
                    out.flush();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Writer getMsgpackInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers) throws IOException {

        MessagePack mp = new MessagePack();
        Packer p = mp.createPacker(out);

        Map<Class, WriteHandler<?,?>> handlers = handlers(customHandlers);

        final MsgpackEmitter emitter = new MsgpackEmitter(p, handlers);

        setSubHandler(handlers, emitter);

	    final WriteCache wc = new WriteCache(true);

        return new Writer() {
            @Override
            public void write(Object o) {
                try {
                    emitter.emit(o, false, wc.init());
                    out.flush();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
