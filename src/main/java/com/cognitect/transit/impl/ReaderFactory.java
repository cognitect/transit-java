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

    public static Map<String, Decoder> defaultDecoders() {

        Map<String, Decoder> decoders = new HashMap<String, Decoder>();

        decoders.put(":", new Decoders.KeywordDecoder());
        decoders.put("$", new Decoders.SymbolDecoder());
        decoders.put("i", new Decoders.IntegerDecoder());
        decoders.put("?", new Decoders.BooleanDecoder());
        decoders.put("_", new Decoders.NullDecoder());
        decoders.put("f", new Decoders.BigDecimalDecoder());
        decoders.put("n", new Decoders.BigIntegerDecoder());
        decoders.put("d", new Decoders.DoubleDecoder());
        decoders.put("c", new Decoders.CharacterDecoder());
        decoders.put("t", new Decoders.VerboseTimeDecoder());
        decoders.put("m", new Decoders.TimeDecoder());
        decoders.put("r", new Decoders.URIDecoder());
        decoders.put("u", new Decoders.UUIDDecoder());
        decoders.put("b", new Decoders.BinaryDecoder());
        decoders.put("\'", new Decoders.IdentityDecoder());
        decoders.put("set", new Decoders.SetDecoder());
        decoders.put("list", new Decoders.ListDecoder());
        decoders.put("ratio", new Decoders.RatioDecoder());
        decoders.put("cmap", new Decoders.CmapDecoder());
        decoders.put("link", new Decoders.LinkDecoder());
        decoders.put("ints", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.INTS));
        decoders.put("longs", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.LONGS));
        decoders.put("floats", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.FLOATS));
        decoders.put("doubles", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.DOUBLES));
        decoders.put("bools", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.BOOLS));
        decoders.put("shorts", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.SHORTS));
        decoders.put("chars", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.CHARS));

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

    private static void disallowOverridingGroundTypes(Map<String, Decoder> decoders) {
        if (decoders != null) {
            String groundTypeTags[] = {"_", "s", "?", "i", "d", "b", "'", "map", "array"};
            for (String tag : groundTypeTags) {
                if (decoders.containsKey(tag)) {
                    throw new IllegalArgumentException("Cannot override decoding for transit ground type, tag " + tag);
                }
            }
        }
    }

    private static Map<String, Decoder> decoders(Map<String, Decoder> customDecoders) {
        disallowOverridingGroundTypes(customDecoders);
        Map<String, Decoder> decoders = defaultDecoders();
        if(customDecoders != null) {
            Iterator<Map.Entry<String, Decoder>> i = customDecoders.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<String, Decoder> e = i.next();
                decoders.put(e.getKey(), e.getValue());
            }
        }
        return decoders;
    }

    private static DefaultDecoder defaultDecoder(DefaultDecoder customDefaultDecoder) {
        return customDefaultDecoder != null ? customDefaultDecoder : defaultDefaultDecoder();
    }

    public static Reader getJsonInstance(InputStream in,
                                     Map<String, Decoder> customDecoders,
                                     DefaultDecoder customDefaultDecoder) {
        return new JsonReaderImpl(in, decoders(customDecoders), defaultDecoder(customDefaultDecoder));
    }

    public static Reader getMsgpackInstance(InputStream in,
                                            Map<String, Decoder> customDecoders,
                                            DefaultDecoder customDefaultDecoder) {
        return new MsgPackReaderImpl(in, decoders(customDecoders), defaultDecoder(customDefaultDecoder));
    }

    private abstract static class ReaderImpl implements Reader, ReaderSPI {

        InputStream in;
        Map<String, Decoder> decoders;
        DefaultDecoder defaultDecoder;
        MapBuilder mapBuilder;
        ListBuilder listBuilder;
        ArrayBuilder arrayBuilder;
        SetBuilder setBuilder;
        ReadCache cache;
        AbstractParser p;
        boolean initialized;

        public ReaderImpl(InputStream in, Map<String, Decoder> decoders, DefaultDecoder defaultDecoder) {
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
            Iterator<Decoder> i = decoders.values().iterator();
            while(i.hasNext()) {
                Decoder d = i.next();
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

        public JsonReaderImpl(InputStream in, Map<String, Decoder> decoders, DefaultDecoder defaultDecoder) {
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

        public MsgPackReaderImpl(InputStream in, Map<String, Decoder> decoders, DefaultDecoder defaultDecoder) {
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
