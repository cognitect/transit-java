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

    public static Map<String, ReadHandler> defaultDecoders() {

        Map<String, ReadHandler> decoders = new HashMap<String, ReadHandler>();

        decoders.put(":", new ReadHandlers.KeywordDecoder());
        decoders.put("$", new ReadHandlers.SymbolDecoder());
        decoders.put("i", new ReadHandlers.IntegerDecoder());
        decoders.put("?", new ReadHandlers.BooleanDecoder());
        decoders.put("_", new ReadHandlers.NullDecoder());
        decoders.put("f", new ReadHandlers.BigDecimalDecoder());
        decoders.put("n", new ReadHandlers.BigIntegerDecoder());
        decoders.put("d", new ReadHandlers.DoubleDecoder());
        decoders.put("c", new ReadHandlers.CharacterDecoder());
        decoders.put("t", new ReadHandlers.VerboseTimeDecoder());
        decoders.put("m", new ReadHandlers.TimeDecoder());
        decoders.put("r", new ReadHandlers.URIDecoder());
        decoders.put("u", new ReadHandlers.UUIDDecoder());
        decoders.put("b", new ReadHandlers.BinaryDecoder());
        decoders.put("\'", new ReadHandlers.IdentityDecoder());
        decoders.put("set", new ReadHandlers.SetDecoder());
        decoders.put("list", new ReadHandlers.ListDecoder());
        decoders.put("ratio", new ReadHandlers.RatioDecoder());
        decoders.put("cmap", new ReadHandlers.CmapDecoder());
        decoders.put("link", new ReadHandlers.LinkDecoder());
        decoders.put("ints", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.INTS));
        decoders.put("longs", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.LONGS));
        decoders.put("floats", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.FLOATS));
        decoders.put("doubles", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.DOUBLES));
        decoders.put("bools", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.BOOLS));
        decoders.put("shorts", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.SHORTS));
        decoders.put("chars", new ReadHandlers.PrimitiveArrayDecoder(ReadHandlers.PrimitiveArrayDecoder.CHARS));

        return decoders;
    }

    public static DefaultDecoder defaultDefaultDecoder() {
        return new DefaultDecoder() {
            @Override
            public Object decode(String tag, Object rep) {
                return TransitFactory.taggedValue(tag, rep);
            }
        };
    }

    private static void disallowOverridingGroundTypes(Map<String, ReadHandler> decoders) {
        if (decoders != null) {
            String groundTypeTags[] = {"_", "s", "?", "i", "d", "b", "'", "map", "array"};
            for (String tag : groundTypeTags) {
                if (decoders.containsKey(tag)) {
                    throw new IllegalArgumentException("Cannot override decoding for transit ground type, tag " + tag);
                }
            }
        }
    }

    private static Map<String, ReadHandler> decoders(Map<String, ReadHandler> customDecoders) {
        disallowOverridingGroundTypes(customDecoders);
        Map<String, ReadHandler> decoders = defaultDecoders();
        if(customDecoders != null) {
            Iterator<Map.Entry<String, ReadHandler>> i = customDecoders.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<String, ReadHandler> e = i.next();
                decoders.put(e.getKey(), e.getValue());
            }
        }
        return decoders;
    }

    private static DefaultDecoder defaultDecoder(DefaultDecoder customDefaultDecoder) {
        return customDefaultDecoder != null ? customDefaultDecoder : defaultDefaultDecoder();
    }

    public static Reader getJsonInstance(InputStream in,
                                     Map<String, ReadHandler> customDecoders,
                                     DefaultDecoder customDefaultDecoder) {
        return new JsonReaderImpl(in, decoders(customDecoders), defaultDecoder(customDefaultDecoder));
    }

    public static Reader getMsgpackInstance(InputStream in,
                                            Map<String, ReadHandler> customDecoders,
                                            DefaultDecoder customDefaultDecoder) {
        return new MsgPackReaderImpl(in, decoders(customDecoders), defaultDecoder(customDefaultDecoder));
    }

    private abstract static class ReaderImpl implements Reader, ReaderSPI {

        InputStream in;
        Map<String, ReadHandler> decoders;
        DefaultDecoder defaultDecoder;
        MapBuilder mapBuilder;
        ListBuilder listBuilder;
        ArrayBuilder arrayBuilder;
        SetBuilder setBuilder;
        ReadCache cache;
        AbstractParser p;
        boolean initialized;

        public ReaderImpl(InputStream in, Map<String, ReadHandler> decoders, DefaultDecoder defaultDecoder) {
            this.initialized = false;
            this.in = in;
            this.decoders = decoders;
            this.defaultDecoder = defaultDecoder;
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
            Iterator<ReadHandler> i = decoders.values().iterator();
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

        public JsonReaderImpl(InputStream in, Map<String, ReadHandler> decoders, DefaultDecoder defaultDecoder) {
            super(in, decoders, defaultDecoder);
        }

        @Override
        protected AbstractParser createParser() {
            try {
                JsonFactory jf = new JsonFactory();
                return new JsonParser(jf.createParser(in), decoders, defaultDecoder,
                        mapBuilder, listBuilder, arrayBuilder, setBuilder);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class MsgPackReaderImpl extends ReaderImpl {

        public MsgPackReaderImpl(InputStream in, Map<String, ReadHandler> decoders, DefaultDecoder defaultDecoder) {
            super(in, decoders, defaultDecoder);
        }

        @Override
        protected AbstractParser createParser() {
            MessagePack mp = new MessagePack();
            return new MsgpackParser(mp.createUnpacker(in), decoders, defaultDecoder,
                    mapBuilder, listBuilder, arrayBuilder, setBuilder);
        }
    }
}
