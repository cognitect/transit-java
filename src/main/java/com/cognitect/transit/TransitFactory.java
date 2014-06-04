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
    public static enum Format { JSON, MSGPACK, JSON_HUMAN, JSON_MACHINE }

    public static Writer writer(Format type, OutputStream out)  throws IOException, IllegalArgumentException {
        return writer(type, out, null, true);
    }

    public static Writer writer(Format type, OutputStream out, boolean enableCaching) { return writer(type, out, null, enableCaching); }

    public static Writer writer(Format type, OutputStream out, Map<Class, Handler> customHandlers) { return writer(type, out, customHandlers, true); }

    public static Writer writer(Format type, OutputStream out, Map<Class, Handler> customHandlers, boolean enableCaching) {
        try {
            customHandlers = (customHandlers == null) ? new HashMap<Class, Handler>() : customHandlers;

            switch (type) {
                case JSON_HUMAN:
                case JSON:
                    customHandlers.put(Map.class, new Handlers.HumanModeTimeHandler());
                    return WriterImpl.getJsonInstance(out, customHandlers, false);
                case JSON_MACHINE:
                    customHandlers.put(Map.class, new Handlers.MachineModeMapHandler());
                    return WriterImpl.getJsonInstance(out, customHandlers, enableCaching);
                case MSGPACK:
                    return WriterImpl.getMsgpackInstance(out, customHandlers, enableCaching);
                default:
                    throw new IllegalArgumentException("Unknown Writer type: " + type.toString());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /*

            if (wmode == TransitFactory.Wmode.MACHINE) {

            // TODO: make Date handler emit longs
            return getJsonInstance(out, customHandlers, enableCaching);
        }
        else if (wmode == TransitFactory.Wmode.HUMAN) {
            // TODO: make Date handler emit readable dates
            return getJsonInstance(out, customHandlers, false);
        }

        return getJsonInstance(out, customHandlers, enableCaching);


     */

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

    public static Map defaultDecoders() { return ReaderImpl.defaultDecoders(); }
    public static Map defaultHandlers() { return WriterImpl.defaultHandlers(); }
    public static MapBuilder defaultMapBuilder() { return new MapBuilderImpl(); }
    public static ArrayBuilder defaultArrayBuilder() { return new ArrayBuilderImpl(); }
    public static ListBuilder defaultListBuilder() { return new ListBuilderImpl(); }
    public static SetBuilder defaultSetBuilder() { return new SetBuilderImpl(); }
}
