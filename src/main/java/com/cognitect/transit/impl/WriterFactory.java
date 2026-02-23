// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;
import com.cognitect.transit.Writer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Function;

public class WriterFactory {

    private static WriteHandlerMap buildWriteHandlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        return new WriteHandlerMap(customHandlers);
    }

    private static WriteHandlerMap verboseHandlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        return buildWriteHandlerMap(customHandlers).verboseWriteHandlerMap();
    }

    public static <T> Writer<T> getJsonInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers,  WriteHandler<?, ?> defaultWriteHandler, boolean verboseMode) throws IOException {
        return getJsonInstance(out, customHandlers, defaultWriteHandler, verboseMode, null);
    }

    public static <T> Writer<T> getJsonInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers,  WriteHandler<?, ?> defaultWriteHandler, boolean verboseMode, Function<Object,Object> transform) throws IOException {

        JsonGenerator gen = new JsonFactory().createGenerator(out);
        final JsonEmitter emitter;

        if (verboseMode) {
            emitter = new JsonVerboseEmitter(gen, verboseHandlerMap(customHandlers), defaultWriteHandler, transform);
        } else {
            emitter = new JsonEmitter(gen, buildWriteHandlerMap(customHandlers), defaultWriteHandler, transform);
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

    public static <T> Writer<T> getMsgpackInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers, WriteHandler<?, ?> defaultWriteHandler) throws IOException {
        return getMsgpackInstance(out, customHandlers, defaultWriteHandler, null);
    }

    public static <T> Writer<T> getMsgpackInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers, WriteHandler<?, ?> defaultWriteHandler, Function<Object,Object> transform) throws IOException {

        MessagePacker packer = MessagePack.newDefaultPacker(out);

        final MsgpackEmitter emitter = new MsgpackEmitter(packer, buildWriteHandlerMap(customHandlers), defaultWriteHandler, transform);

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

    public static WriteHandler defaultDefaultHandler() {
        return new WriteHandler() {
            private String throwException(Object o) {
                throw new RuntimeException("Not supported " + o);
            }

            @Override
            public String tag(Object o) {
                return throwException(o);
            }

            @Override
            public Object rep(Object o) {
                return throwException(o);
            }

            @Override
            public String stringRep(Object o) {
                return throwException(o);
            }

            @Override
            public WriteHandler getVerboseHandler() {
                return null;
            }
        };
    }

    @Deprecated
    public static <T> Writer<T> getMsgpackInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers) throws IOException {
        return getMsgpackInstance(out, customHandlers, null);
    }

    @Deprecated
    public static <T> Writer<T> getJsonInstance(final OutputStream out, Map<Class, WriteHandler<?,?>> customHandlers, boolean verboseMode) throws IOException {
        return getJsonInstance(out, customHandlers, null, verboseMode);
    }

}
