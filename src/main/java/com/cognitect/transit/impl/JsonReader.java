package com.cognitect.transit.impl;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.impl.decode.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.cognitect.transit.Reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonReader implements Reader {

    private final InputStream in;
    private final Parser p;
    private final Map<String, Decoder> decoders = new HashMap<String, Decoder>();

    public JsonReader(InputStream in) throws IOException {

        this.in = in;
        JsonFactory jf = new JsonFactory();

        this.decoders.put(":", new KeywordDecoder());
        this.decoders.put("$", new SymbolDecoder());
        this.decoders.put("i", new IntegerDecoder());
        this.decoders.put("?", new BooleanDecoder());
        this.decoders.put("_", new NullDecoder());
        this.decoders.put("f", new BigDecimalDecoder());
        this.decoders.put("d", new DoubleDecoder());
        this.decoders.put("c", new CharacterDecoder());
        this.decoders.put("t", new TimeDecoder());
        this.decoders.put("r", new URIDecoder());
        this.decoders.put("u", new UUIDDecoder());
        this.decoders.put("b", new BinaryDecoder());
        this.decoders.put("set", new SetDecoder());
        this.decoders.put("list", new ListDecoder());
        this.decoders.put("ints", new PrimitiveArrayDecoder(PrimitiveArrayDecoder.INTS));
        this.decoders.put("longs", new PrimitiveArrayDecoder(PrimitiveArrayDecoder.LONGS));
        this.decoders.put("floats", new PrimitiveArrayDecoder(PrimitiveArrayDecoder.FLOATS));
        this.decoders.put("doubles", new PrimitiveArrayDecoder(PrimitiveArrayDecoder.DOUBLES));
        this.decoders.put("bools", new PrimitiveArrayDecoder(PrimitiveArrayDecoder.BOOLS));
        this.decoders.put("shorts", new PrimitiveArrayDecoder(PrimitiveArrayDecoder.SHORTS));
        this.decoders.put("chars", new PrimitiveArrayDecoder(PrimitiveArrayDecoder.CHARS));

        this.p = new JsonParser(jf.createParser(in), decoders);
    }

    public JsonReader setCustomDecoder(String key, Decoder d) {

        this.decoders.put(key, d);
        return this;
    }

    @Override
    public Object read() throws IOException {

        return p.parse(null);
    }
}
