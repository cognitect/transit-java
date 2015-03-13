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

    private static Map<Map<Class, WriteHandler<?,?>>, WriteHandlerSet> handlerCache = new HashMap<Map<Class, WriteHandler<?,?>>, WriteHandlerSet>();
    private static Map<Map<Class, WriteHandler<?,?>>, WriteHandlerSet> verboseHandlerCache = new HashMap<Map<Class, WriteHandler<?,?>>, WriteHandlerSet>();

    public static Map<Class, WriteHandler<?,?>> defaultHandlers() {

        Map<Class, WriteHandler<?,?>> handlers = new HashMap<Class, WriteHandler<?,?>>();

        WriteHandler integerHandler = new WriteHandlers.IntegerWriteHandler();
        WriteHandler uriHandler = new WriteHandlers.ToStringWriteHandler("r");

        handlers.put(Boolean.class, new WriteHandlers.BooleanWriteHandler());
        handlers.put(null, new WriteHandlers.NullWriteHandler());
        handlers.put(String.class, new WriteHandlers.ToStringWriteHandler("s"));
        handlers.put(Integer.class, integerHandler);
        handlers.put(Long.class, integerHandler);
        handlers.put(Short.class, integerHandler);
        handlers.put(Byte.class, integerHandler);
        handlers.put(BigInteger.class, new WriteHandlers.ToStringWriteHandler("n"));
        handlers.put(Float.class, new WriteHandlers.FloatWriteHandler());
        handlers.put(Double.class, new WriteHandlers.DoubleWriteHandler());
        handlers.put(Map.class, new WriteHandlers.MapWriteHandler());
        handlers.put(BigDecimal.class, new WriteHandlers.ToStringWriteHandler("f"));
        handlers.put(Character.class, new WriteHandlers.ToStringWriteHandler("c"));
        handlers.put(Keyword.class, new WriteHandlers.KeywordWriteHandler());
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
        handlers.put(LinkImpl.class, new WriteHandlers.LinkWriteHandler());
        handlers.put(Quote.class, new WriteHandlers.QuoteAbstractEmitter());
        handlers.put(TaggedValue.class, new WriteHandlers.TaggedValueWriteHandler());
        handlers.put(Object.class, new WriteHandlers.ObjectWriteHandler());

        return handlers;
    }

    private static WriteHandlerSet handlers(Map<Class, WriteHandler<?,?>> customHandlers) {
        if (handlerCache.containsKey(customHandlers)) {
            return handlerCache.get(customHandlers);
        }

        synchronized (WriterFactory.class) {
            if (handlerCache.containsKey(customHandlers)) {
                return handlerCache.get(customHandlers);
            } else {
                Map<Class, WriteHandler<?, ?>> handlers = defaultHandlers();
                if (customHandlers != null) {
                    handlers.putAll(customHandlers);
                }
                WriteHandlerSet writeHandlerSet = new WriteHandlerSet(handlers);
                handlerCache.put(customHandlers, writeHandlerSet);
                return writeHandlerSet;
            }
        }
    }

    private static WriteHandlerSet verboseHandlers(Map<Class, WriteHandler<?,?>> customHandlers) {
        if (verboseHandlerCache.containsKey(customHandlers)) {
            return verboseHandlerCache.get(customHandlers);
        }

        synchronized (WriterFactory.class) {
            if (verboseHandlerCache.containsKey(customHandlers)) {
                return verboseHandlerCache.get(customHandlers);
            } else {
                WriteHandlerSet writeHandlerSet = handlers(customHandlers).getVerboseHandlerSet();
                verboseHandlerCache.put(customHandlers, writeHandlerSet);
                return writeHandlerSet;
            }
        }
    }

    public static <T> Writer<T> getJsonInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers, boolean verboseMode) throws IOException {

        JsonFactory jf = new JsonFactory();
        JsonGenerator gen = jf.createGenerator(out);
        WriteHandlerSet handlers;
        final JsonEmitter emitter;

        if (verboseMode) {
            handlers = verboseHandlers(customHandlers);
            emitter = new JsonVerboseEmitter(gen, handlers);
        } else {
            handlers = handlers(customHandlers);
            emitter = new JsonEmitter(gen, handlers);
        }

        final WriteCache wc = new WriteCache(!verboseMode);

        return new Writer<T>() {
            @Override
            public void write(T o) {
                try {
                    emitter.emit(o, false, wc.init());
                    out.flush();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static <T> Writer<T> getMsgpackInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers) throws IOException {

        MessagePack mp = new MessagePack();
        Packer p = mp.createPacker(out);

        final MsgpackEmitter emitter = new MsgpackEmitter(p, handlers(customHandlers));

	    final WriteCache wc = new WriteCache(true);

        return new Writer<T>() {
            @Override
            public void write(T o) {
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
