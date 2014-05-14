// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.ListBuilder;
import com.cognitect.transit.MapBuilder;
import com.cognitect.transit.Reader;
import com.fasterxml.jackson.core.JsonFactory;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReaderImpl {

    private static Map<String, Decoder> defaultDecoders() {

        Map<String, Decoder> decoders = new HashMap<String, Decoder>();

        decoders.put(":", new Decoders.KeywordDecoder());
        decoders.put("$", new Decoders.SymbolDecoder());
        decoders.put("i", new Decoders.IntegerDecoder());
        decoders.put("?", new Decoders.BooleanDecoder());
        decoders.put("_", new Decoders.NullDecoder());
        decoders.put("f", new Decoders.BigDecimalDecoder());
        decoders.put("d", new Decoders.DoubleDecoder());
        decoders.put("c", new Decoders.CharacterDecoder());
        decoders.put("t", new Decoders.TimeDecoder());
        decoders.put("r", new Decoders.URIDecoder());
        decoders.put("u", new Decoders.UUIDDecoder());
        decoders.put("b", new Decoders.BinaryDecoder());
        decoders.put("\'", new Decoders.IdentityDecoder());
        decoders.put("set", new Decoders.SetDecoder());
        decoders.put("list", new Decoders.ListDecoder());
        decoders.put("ratio", new Decoders.RatioDecoder());
        decoders.put("cmap", new Decoders.CmapDecoder());
        decoders.put("ints", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.INTS));
        decoders.put("longs", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.LONGS));
        decoders.put("floats", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.FLOATS));
        decoders.put("doubles", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.DOUBLES));
        decoders.put("bools", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.BOOLS));
        decoders.put("shorts", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.SHORTS));
        decoders.put("chars", new Decoders.PrimitiveArrayDecoder(Decoders.PrimitiveArrayDecoder.CHARS));

        return decoders;
    }

    private static Map<String, Decoder> decoders(Map<String, Decoder> customDecoders) {
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

    private static void setBuilders(Map<String, Decoder> decoders, MapBuilder mapBuilder, ListBuilder listBuilder) {
        Iterator<Decoder> i = decoders.values().iterator();
        while(i.hasNext()) {
            Decoder d = i.next();
            if(d instanceof BuilderAware)
                ((BuilderAware)d).setBuilders(mapBuilder, listBuilder);
        }
    }

    public static Reader getJsonInstance(InputStream in,
                                         Map<String, Decoder> customDecoders,
                                         MapBuilder mapBuilder, ListBuilder listBuilder) throws IOException {

        JsonFactory jf = new JsonFactory();

        Map<String, Decoder> decoders = decoders(customDecoders);

        setBuilders(decoders, mapBuilder, listBuilder);

        final Parser p = new JsonParser(jf.createParser(in), decoders, mapBuilder, listBuilder);
	    final ReadCache cache = new ReadCache();
        return new Reader() {
	        public Object read() throws IOException {
                return p.parse(cache.init());
            }
        };
    }

    public static Reader getMsgpackInstance(InputStream in,
                                            Map<String, Decoder> customDecoders,
                                            MapBuilder mapBuilder, ListBuilder listBuilder) throws IOException {

        MessagePack mp = new MessagePack();

        Map<String, Decoder> decoders = decoders(customDecoders);

        setBuilders(decoders, mapBuilder, listBuilder);

        final Parser p = new MsgpackParser(mp.createUnpacker(in), decoders, mapBuilder, listBuilder);
	    final ReadCache cache = new ReadCache();
        return new Reader() {
            public Object read() throws IOException {
                return p.parse(cache.init());
            }
        };
    }
}
