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

        handlers.put(":", new ReadHandlers.KeywordDecoder());
        handlers.put("$", new ReadHandlers.SymbolDecoder());
        handlers.put("i", new ReadHandlers.IntegerDecoder());
        handlers.put("?", new ReadHandlers.BooleanDecoder());
        handlers.put("_", new ReadHandlers.NullDecoder());
        handlers.put("f", new ReadHandlers.BigDecimalDecoder());
        handlers.put("n", new ReadHandlers.BigIntegerDecoder());
        handlers.put("d", new ReadHandlers.DoubleDecoder());
        handlers.put("c", new ReadHandlers.CharacterDecoder());
        handlers.put("t", new ReadHandlers.VerboseTimeDecoder());
        handlers.put("m", new ReadHandlers.TimeDecoder());
        handlers.put("r", new ReadHandlers.URIDecoder());
        handlers.put("u", new ReadHandlers.UUIDDecoder());
        handlers.put("b", new ReadHandlers.BinaryDecoder());
        handlers.put("\'", new ReadHandlers.IdentityDecoder());
        handlers.put("set", new ReadHandlers.SetDecoder());
        handlers.put("list", new ReadHandlers.ListDecoder());
        handlers.put("ratio", new ReadHandlers.RatioDecoder());
        handlers.put("cmap", new ReadHandlers.CmapDecoder());
        handlers.put("link", new ReadHandlers.LinkDecoder());
        handlers.put("ints", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.INTS));
        handlers.put("longs", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.LONGS));
        handlers.put("floats", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.FLOATS));
        handlers.put("doubles", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.DOUBLES));
        handlers.put("bools", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.BOOLS));
        handlers.put("shorts", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.SHORTS));
        handlers.put("chars", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.CHARS));

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
        ListBuilder listBuilder;
        ArrayBuilder arrayBuilder;
        SetBuilder setBuilder;
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
        public Reader setBuilders(MapBuilder mapBuilder, ListBuilder listBuilder, ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
            if (initialized) throw new IllegalStateException("Cannot set builders after read has been called");
            this.mapBuilder = mapBuilder;
            this.listBuilder = listBuilder;
            this.arrayBuilder = arrayBuilder;
            this.setBuilder = setBuilder;
            return this;
        }

        private void ensureBuilders() {
            if (mapBuilder == null) mapBuilder = new MapBuilderImpl();
            if (listBuilder == null) listBuilder = new ListBuilderImpl();
            if (arrayBuilder == null) arrayBuilder = new ArrayBuilderImpl();
            if (setBuilder == null) setBuilder = new SetBuilderImpl();
        }

        private void setBuilders() {
            Iterator<ReadHandler> i = handlers.values().iterator();
            while(i.hasNext()) {
                ReadHandler d = i.next();
                if(d instanceof BuilderAware)
                    ((BuilderAware)d).setBuilders(mapBuilder, listBuilder, arrayBuilder, setBuilder);
            }
        }

        protected void initialize() {
            ensureBuilders();
            setBuilders();
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
                        mapBuilder, listBuilder, arrayBuilder, setBuilder);
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
                    mapBuilder, listBuilder, arrayBuilder, setBuilder);
        }
    }
}
