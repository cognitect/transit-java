// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import com.fasterxml.jackson.core.JsonFactory;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReaderImpl {

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

    private static Map<String, Decoder> mergeDecoders(Map<String, Decoder> customDecoders) {
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

    private static void setBuilders(Map<String, Decoder> decoders,
                                    MapBuilder mapBuilder, ListBuilder listBuilder,
                                    ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
        Iterator<Decoder> i = decoders.values().iterator();
        while(i.hasNext()) {
            Decoder d = i.next();
            if(d instanceof BuilderAware)
                ((BuilderAware)d).setBuilders(mapBuilder, listBuilder, arrayBuilder, setBuilder);
        }
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

    private static Map<String, Decoder> configureDecoders(Map<String, Decoder> customDecoders,
                                                          MapBuilder mapBuilder,
                                                          ListBuilder listBuilder,
                                                          ArrayBuilder arrayBuilder,
                                                          SetBuilder setBuilder) {
        disallowOverridingGroundTypes(customDecoders);
        Map<String, Decoder> decoders = mergeDecoders(customDecoders);
        setBuilders(decoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);
        return decoders;
    }

    public static Reader createReader(final AbstractParser p) {
        final ReadCache cache = new ReadCache();
        return new Reader() {
            public Object read() {
                try {
                    return p.parse(cache.init());
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Reader getJsonInstance(InputStream in,
                                         Map<String, Decoder> customDecoders,
                                         DefaultDecoder customDefaultDecoder,
                                         MapBuilder mapBuilder, ListBuilder listBuilder,
                                         ArrayBuilder arrayBuilder, SetBuilder setBuilder) throws IOException {

        Map<String, Decoder> decoders =
                configureDecoders(customDecoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);

        JsonFactory jf = new JsonFactory();
        AbstractParser p = new JsonParser(jf.createParser(in), decoders, customDefaultDecoder,
                mapBuilder, listBuilder, arrayBuilder, setBuilder);

        return createReader(p);
    }

    public static Reader getMsgpackInstance(InputStream in,
                                            Map<String, Decoder> customDecoders,
                                            DefaultDecoder customDefaultDecoder,
                                            MapBuilder mapBuilder, ListBuilder listBuilder,
                                            ArrayBuilder arrayBuilder, SetBuilder setBuilder) throws IOException {

        Map<String, Decoder> decoders =
                configureDecoders(customDecoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);

        MessagePack mp = new MessagePack();
        AbstractParser p = new MsgpackParser(mp.createUnpacker(in), decoders, customDefaultDecoder,
                mapBuilder, listBuilder, arrayBuilder, setBuilder);

        return createReader(p);
    }
}
