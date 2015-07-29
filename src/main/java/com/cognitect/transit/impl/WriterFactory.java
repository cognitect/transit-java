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

    private static Map<Map<Class, WriteHandler<?,?>>, WriteHandlerMap> newHandlerCache = new Cache<Map<Class, WriteHandler<?,?>>, WriteHandlerMap>();
    private static Map<Map<Class, WriteHandler<?,?>>, WriteHandlerMap> newVerboseHandlerCache = new Cache<Map<Class, WriteHandler<?,?>>, WriteHandlerMap>();


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

    private static WriteHandlerMap handlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        if (customHandlers instanceof WriteHandlerMap)
            return (WriteHandlerMap) customHandlers;

        if (newHandlerCache.containsKey(customHandlers)) {
            return newHandlerCache.get(customHandlers);
        }

        synchronized (WriterFactory.class) {
            if (newHandlerCache.containsKey(customHandlers)) {
                return newHandlerCache.get(customHandlers);
            } else {
                WriteHandlerMap writeHandlerMap = new WriteHandlerMap(customHandlers);
                newHandlerCache.put(customHandlers, writeHandlerMap);
                return writeHandlerMap;
            }
        }
    }

    private static WriteHandlerMap verboseHandlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        if (customHandlers instanceof WriteHandlerMap) {
            return ((WriteHandlerMap) customHandlers).verboseWriteHandlerMap();
        }

        if (newVerboseHandlerCache.containsKey(customHandlers)) {
            return newVerboseHandlerCache.get(customHandlers);
        }

        synchronized (WriterFactory.class) {
            if (newVerboseHandlerCache.containsKey(customHandlers)) {
                return newVerboseHandlerCache.get(customHandlers);
            } else {
                WriteHandlerMap verboseHandlerMap = handlerMap(customHandlers).verboseWriteHandlerMap();
                newVerboseHandlerCache.put(customHandlers, verboseHandlerMap);
                return verboseHandlerMap;
            }
        }
    }

    public static <T> Writer<T> getJsonInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers, boolean verboseMode) throws IOException {

        JsonGenerator gen = new JsonFactory().createGenerator(out);
        final JsonEmitter emitter;

        if (verboseMode) {
            emitter = new JsonVerboseEmitter(gen, verboseHandlerMap(customHandlers));
        } else {
            emitter = new JsonEmitter(gen, handlerMap(customHandlers));
        }

        final WriteCache writeCache = new WriteCache(!verboseMode);

        return new Writer<T>() {
            @Override
            public void write(T o) {
                try {
                    emitter.emit(o, false, writeCache.init());
                    out.flush();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static <T> Writer<T> getMsgpackInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers) throws IOException {

        Packer packer = new MessagePack().createPacker(out);

        final MsgpackEmitter emitter = new MsgpackEmitter(packer, handlerMap(customHandlers));

        final WriteCache writeCache = new WriteCache(true);

        return new Writer<T>() {
            @Override
            public void write(T o) {
                try {
                    emitter.emit(o, false, writeCache.init());
                    out.flush();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
