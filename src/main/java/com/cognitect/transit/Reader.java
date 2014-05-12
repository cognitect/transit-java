// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import com.cognitect.transit.impl.*;
import com.fasterxml.jackson.core.JsonFactory;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Reader {

    public static enum Format { JSON, MSGPACK }

    public static IReader instance(Format type, InputStream in, Map<String, Decoder> customDecoders) throws IOException, IllegalArgumentException {
        switch (type) {
            case JSON:    return getJsonInstance(in, customDecoders);
            case MSGPACK: return getMsgpackInstance(in, customDecoders);
            default: throw new IllegalArgumentException("Unknown Reader type: " + type.toString());
        }
    }

    protected static Map<String, Decoder> defaultDecoders() {

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

    protected static IReader getJsonInstance(InputStream in, Map<String, Decoder> customDecoders) throws IOException {

        JsonFactory jf = new JsonFactory();

        Map<String, Decoder> decoders = defaultDecoders();
        if(customDecoders != null) {
            Iterator<Map.Entry<String, Decoder>> i = customDecoders.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<String, Decoder> e = i.next();
                decoders.put(e.getKey(), e.getValue());
            }
        }

        final Parser p = new JsonParser(jf.createParser(in), decoders);
        return new IReader() {
            public Object read() throws IOException {
                return p.parse(new ReadCache());
            }
        };
    }

    protected static IReader getMsgpackInstance(InputStream in, Map<String, Decoder> customDecoders) throws IOException {

        MessagePack mp = new MessagePack();

        Map<String, Decoder> decoders = defaultDecoders();
        if(customDecoders != null) {
            Iterator<Map.Entry<String, Decoder>> i = customDecoders.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<String, Decoder> e = i.next();
                decoders.put(e.getKey(), e.getValue());
            }
        }

        final Parser p = new MsgpackParser(mp.createUnpacker(in), decoders);
        return new IReader() {
            public Object read() throws IOException {
                return p.parse(new ReadCache());
            }
        };
    }
}
