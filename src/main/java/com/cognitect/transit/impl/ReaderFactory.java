// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import com.fasterxml.jackson.core.JsonFactory;
import org.msgpack.MessagePack;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReaderFactory {

    public static Map<String, ReadHandler> defaultHandlers() {

        Map<String, ReadHandler> handlers = new HashMap<String, ReadHandler>();

        handlers.put(":", new ReadHandlers.KeywordReadHandler());
        handlers.put("$", new ReadHandlers.SymbolReadHandler());
        handlers.put("i", new ReadHandlers.IntegerReadHandler());
        handlers.put("?", new ReadHandlers.BooleanReadHandler());
        handlers.put("_", new ReadHandlers.NullReadHandler());
        handlers.put("f", new ReadHandlers.BigDecimalReadHandler());
        handlers.put("n", new ReadHandlers.BigIntegerReadHandler());
        handlers.put("d", new ReadHandlers.DoubleReadHandler());
        handlers.put("c", new ReadHandlers.CharacterReadHandler());
        handlers.put("t", new ReadHandlers.VerboseTimeReadHandler());
        handlers.put("m", new ReadHandlers.TimeReadHandler());
        handlers.put("r", new ReadHandlers.URIReadHandler());
        handlers.put("u", new ReadHandlers.UUIDReadHandler());
        handlers.put("b", new ReadHandlers.BinaryReadHandler());
        handlers.put("\'", new ReadHandlers.IdentityReadHandler());
        handlers.put("set", new ReadHandlers.SetReadHandler());
        handlers.put("list", new ReadHandlers.ListReadHandler());
        handlers.put("ratio", new ReadHandlers.RatioReadHandler());
        handlers.put("cmap", new ReadHandlers.CmapReadHandler());
        handlers.put("link", new ReadHandlers.LinkReadHandler());
        handlers.put("ints", new ReadHandlers.PrimitiveArrayReadHandler(ReadHandlers.PrimitiveArrayReadHandler.INTS));
        handlers.put("longs", new ReadHandlers.PrimitiveArrayReadHandler(ReadHandlers.PrimitiveArrayReadHandler.LONGS));
        handlers.put("floats", new ReadHandlers.PrimitiveArrayReadHandler(ReadHandlers.PrimitiveArrayReadHandler.FLOATS));
        handlers.put("doubles", new ReadHandlers.PrimitiveArrayReadHandler(ReadHandlers.PrimitiveArrayReadHandler.DOUBLES));
        handlers.put("bools", new ReadHandlers.PrimitiveArrayReadHandler(ReadHandlers.PrimitiveArrayReadHandler.BOOLS));
        handlers.put("shorts", new ReadHandlers.PrimitiveArrayReadHandler(ReadHandlers.PrimitiveArrayReadHandler.SHORTS));
        handlers.put("chars", new ReadHandlers.PrimitiveArrayReadHandler(ReadHandlers.PrimitiveArrayReadHandler.CHARS));

        return handlers;
    }

    public static DefaultReadHandler defaultDefaultHandler() {
        return new DefaultReadHandler() {
            @Override
            public Object fromRep(String tag, Object rep) {
                return TransitFactory.taggedValue(tag, rep);
            }
        };
    }

    private static void disallowOverridingGroundTypes(Map<String, ReadHandler> handlers) {
        if (handlers != null) {
            String groundTypeTags[] = {"_", "s", "?", "i", "d", "b", "'", "map", "array"};
            for (String tag : groundTypeTags) {
                if (handlers.containsKey(tag)) {
                    throw new IllegalArgumentException("Cannot override decoding for transit ground type, tag " + tag);
                }
            }
        }
    }

    private static Map<String, ReadHandler> handlers(Map<String, ReadHandler> customDecoders) {
        disallowOverridingGroundTypes(customDecoders);
        Map<String, ReadHandler> handlers = defaultHandlers();
        if(customDecoders != null) {
            Iterator<Map.Entry<String, ReadHandler>> i = customDecoders.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<String, ReadHandler> e = i.next();
                handlers.put(e.getKey(), e.getValue());
            }
        }
        return handlers;
    }

    private static DefaultReadHandler defaultHandler(DefaultReadHandler customDefaultHandler) {
        return customDefaultHandler != null ? customDefaultHandler : defaultDefaultHandler();
    }

    public static Reader getJsonInstance(InputStream in,
                                     Map<String, ReadHandler> customDecoders,
                                     DefaultReadHandler customDefaultHandler) {
        return new JsonReaderImpl(in, handlers(customDecoders), defaultHandler(customDefaultHandler));
    }

    public static Reader getMsgpackInstance(InputStream in,
                                            Map<String, ReadHandler> customDecoders,
                                            DefaultReadHandler customDefaultHandler) {
        return new MsgPackReaderImpl(in, handlers(customDecoders), defaultHandler(customDefaultHandler));
    }

    private abstract static class ReaderImpl implements Reader, ReaderSPI {

        InputStream in;
        Map<String, ReadHandler> handlers;
        DefaultReadHandler defaultHandler;
        MapBuilder mapBuilder;
        ArrayBuilder arrayBuilder;
        ReadCache cache;
        AbstractParser p;
        boolean initialized;

        public ReaderImpl(InputStream in, Map<String, ReadHandler> handlers, DefaultReadHandler defaultHandler) {
            this.initialized = false;
            this.in = in;
            this.handlers = handlers;
            this.defaultHandler = defaultHandler;
            this.cache = new ReadCache();
        }

        @Override
        public Object read() {
            if (!initialized) initialize();
            try {
                return p.parse(cache.init());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Reader setBuilders(MapBuilder mapBuilder, ArrayBuilder arrayBuilder) {
            if (initialized) throw new IllegalStateException("Cannot set builders after read has been called");
            this.mapBuilder = mapBuilder;
            this.arrayBuilder = arrayBuilder;
            return this;
        }

        private void ensureBuilders() {
            if (mapBuilder == null) mapBuilder = new MapBuilderImpl();
            if (arrayBuilder == null) arrayBuilder = new ArrayBuilderImpl();
        }

        protected void initialize() {
            ensureBuilders();
            p = createParser();
            initialized = true;
        }

        protected abstract AbstractParser createParser();
    }

    private static class JsonReaderImpl extends ReaderImpl {

        public JsonReaderImpl(InputStream in, Map<String, ReadHandler> handlers, DefaultReadHandler defaultHandler) {
            super(in, handlers, defaultHandler);
        }

        @Override
        protected AbstractParser createParser() {
            try {
                JsonFactory jf = new JsonFactory();
                return new JsonParser(jf.createParser(in), handlers, defaultHandler,
                        mapBuilder, arrayBuilder);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class MsgPackReaderImpl extends ReaderImpl {

        public MsgPackReaderImpl(InputStream in, Map<String, ReadHandler> handlers, DefaultReadHandler defaultHandler) {
            super(in, handlers, defaultHandler);
        }

        @Override
        protected AbstractParser createParser() {
            MessagePack mp = new MessagePack();
            return new MsgpackParser(mp.createUnpacker(in), handlers, defaultHandler,
                    mapBuilder, arrayBuilder);
        }
    }
}
