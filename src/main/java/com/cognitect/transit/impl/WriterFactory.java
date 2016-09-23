// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;
import com.cognitect.transit.Writer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class WriterFactory {

    private static final Map<Map<Class, WriteHandler<?,?>>, WriteHandlerMap> handlerCache = new Cache<Map<Class, WriteHandler<?,?>>, WriteHandlerMap>();

    private static WriteHandlerMap handlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        if (customHandlers instanceof WriteHandlerMap)
            return new WriteHandlerMap(customHandlers);

        if (handlerCache.containsKey(customHandlers)) {
            return new WriteHandlerMap(handlerCache.get(customHandlers));
        }

        synchronized (handlerCache) {
            if (handlerCache.containsKey(customHandlers)) {
                return new WriteHandlerMap(handlerCache.get(customHandlers));
            } else {
                WriteHandlerMap writeHandlerMap = new WriteHandlerMap(customHandlers);
                handlerCache.put(customHandlers, writeHandlerMap);
                return writeHandlerMap;
            }
        }
    }

    private static WriteHandlerMap verboseHandlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        return handlerMap(customHandlers).verboseWriteHandlerMap();
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
