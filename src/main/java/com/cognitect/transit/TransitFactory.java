// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;


import com.cognitect.transit.impl.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class TransitFactory {
    public static enum Format { JSON, MSGPACK, JSON_VERBOSE }

    public static Writer writer(Format type, OutputStream out)  throws IOException, IllegalArgumentException {
        return writer(type, out, null);
    }

    public static Writer writer(Format type, OutputStream out, Map<Class, Handler> customHandlers) {
        try {
            HashMap<Class, Handler> h = new HashMap<Class, Handler>();
            if (customHandlers != null) h.putAll(customHandlers);
            customHandlers = h;

            switch (type) {
                case MSGPACK:
                    return WriterImpl.getMsgpackInstance(out, customHandlers);
                case JSON:
                    return WriterImpl.getJsonInstance(out, customHandlers, false);
                case JSON_VERBOSE:
                    return WriterImpl.getJsonInstance(out, customHandlers, true);
                default:
                    throw new IllegalArgumentException("Unknown Writer type: " + type.toString());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Reader reader(Format type, InputStream in) throws IOException, IllegalArgumentException {
        return reader(type, in, null);
    }

    public static Reader reader(Format type, InputStream in, Map<String, Decoder> customDecoders) {
        return reader(type, in, customDecoders, new MapBuilderImpl(), new ListBuilderImpl(), new ArrayBuilderImpl(), new SetBuilderImpl());
    }

    public static Reader reader(Format type, InputStream in,
                                Map<String, Decoder> customDecoders,
                                MapBuilder mapBuilder, ListBuilder listBuilder,
                                ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
        try {
            switch (type) {
                case JSON:
                case JSON_VERBOSE:
                    return ReaderImpl.getJsonInstance(in, customDecoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);
                case MSGPACK:
                    return ReaderImpl.getMsgpackInstance(in, customDecoders, mapBuilder, listBuilder, arrayBuilder, setBuilder);
                default:
                    throw new IllegalArgumentException("Unknown Reader type: " + type.toString());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Keyword keyword(Object o) {
        if (o instanceof Keyword)
            return (Keyword) o;
        else if (o instanceof String) {
            String s = (String) o;
            if (s.charAt(0) == ':')
                return new KeywordImpl(s.substring(1));
            else
                return new KeywordImpl(s);
        }
        else throw new IllegalArgumentException("Cannot make keyword from " + o.getClass().getSimpleName());
    }

    public static Symbol symbol(Object o) {
        if (o instanceof Symbol)
            return (Symbol) o;
        else if (o instanceof String) {
            String s = (String) o;
            if (s.charAt(0) == ':')
                return new SymbolImpl(s.substring(1));
            else
                return new SymbolImpl(s);
        }
        else throw new IllegalArgumentException("Cannot make keyword from " + o.getClass().getSimpleName());
    }

    public static TaggedValue taggedValue(String tag, Object rep) {
        return new TaggedValueImpl(tag, rep);
    }

    public static TaggedValue taggedValue(String tag, Object rep, String stringRep) {
        return new TaggedValueImpl(tag, rep, stringRep);
    }

    public static Map defaultDecoders() { return ReaderImpl.defaultDecoders(); }
    public static Map defaultHandlers() { return WriterImpl.defaultHandlers(); }
    public static MapBuilder defaultMapBuilder() { return new MapBuilderImpl(); }
    public static ArrayBuilder defaultArrayBuilder() { return new ArrayBuilderImpl(); }
    public static ListBuilder defaultListBuilder() { return new ListBuilderImpl(); }
    public static SetBuilder defaultSetBuilder() { return new SetBuilderImpl(); }
}
