// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;


import com.cognitect.transit.impl.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class TransitFactory {
    public static enum Format { JSON, MSGPACK }

    public static Writer writer(Format type, OutputStream out)  throws IOException, IllegalArgumentException {
        return writer(type, out, null);
    }

    public static Writer writer(Format type, OutputStream out, Map<Class, Handler> customHandlers) throws IOException, IllegalArgumentException {
        switch (type) {
            case JSON:    return WriterImpl.getJsonInstance(out, customHandlers);
            case MSGPACK: return WriterImpl.getMsgpackInstance(out, customHandlers);
            default: throw new IllegalArgumentException("Unknown Writer type: " + type.toString());
        }
    }

    public static Reader reader(Format type, InputStream in) throws IOException, IllegalArgumentException {
        return reader(type, in, null);
    }

    public static Reader reader(Format type, InputStream in, Map<String, Decoder> customDecoders) throws IOException, IllegalArgumentException {
        return reader(type, in, customDecoders, new MapBuilderImpl(), new ListBuilderImpl(), new ArrayBuilderImpl(), new SetBuilderImpl());
    }

    public static Reader reader(Format type, InputStream in,
                                Map<String, Decoder> customDecoders,
                                MapBuilder mapBuilder, ListBuilder listBuilder,
                                ArrayBuilder arrayBuilder, SetBuilder setBuilder) throws IOException, IllegalArgumentException {
        switch (type) {
            case JSON:    return ReaderImpl.getJsonInstance(in, customDecoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);
            case MSGPACK: return ReaderImpl.getMsgpackInstance(in, customDecoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);
            default: throw new IllegalArgumentException("Unknown Reader type: " + type.toString());
        }
    }

    public static Keyword keyword(Object o) {
        if (o instanceof Keyword)
            return (Keyword) o;
        else if (o instanceof String)
            return new KeywordImpl((String) o);
        else throw new IllegalArgumentException("Cannot make keyword from " + o.getClass().getSimpleName());
    }

    public static Symbol symbol(Object o) {
        if (o instanceof Symbol)
            return (Symbol) o;
        else if (o instanceof String)
            return new SymbolImpl((String) o);
        else throw new IllegalArgumentException("Cannot make keyword from " + o.getClass().getSimpleName());
    }

    public static TaggedValue taggedValue(String tag, Object rep) {
        return new TaggedValueImpl(tag, rep);
    }

    public static TaggedValue taggedValue(String tag, Object rep, String stringRep) {
        return new TaggedValueImpl(tag, rep, stringRep);
    }
}
