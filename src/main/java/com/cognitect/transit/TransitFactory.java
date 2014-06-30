// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;


import com.cognitect.transit.impl.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Main entry point for using transit-java library. Provides methods to construct
 * readers and writers, as well as helpers to make various other values.
 */
public class TransitFactory {
    /**
     * Transit formats
     */
    public static enum Format { JSON, MSGPACK, JSON_VERBOSE }

    /**
     * Creates a writer instance.
     * @param type format to write in
     * @param out output stream to write to
     * @return a Writer
     */
    public static Writer writer(Format type, OutputStream out) {
        try {
            return writer(type, out, null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a writer instance.
     * @param type format to write in
     * @param out output stream to write to
     * @param customHandlers additional Handlers to use in addition
     *                       to or in place of the default Handlers
     * @return a writer
     */
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

    /**
     * Creates a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @return a reader
     */
    public static Reader reader(Format type, InputStream in) {
        return reader(type, in, defaultDefaultDecoder());
    }

    /**
     * Creats a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @param customDecoders a map of custom Decoders to use in addition
     *                       or in place of the default Decoders
     * @return a reader
     */
    public static Reader reader(Format type, InputStream in, Map<String, Decoder> customDecoders) {
        return reader(type, in, customDecoders, defaultDefaultDecoder());
    }

    /**
     * Creats a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @param customDefaultDecoder a DefaultDecoder to use for processing
     *                             encoded values for which there is no decoder;
     *                             if this value is null, reading non-decodable
     *                             values throws an exception
     * @return a reader
     */
    public static Reader reader(Format type, InputStream in, DefaultDecoder customDefaultDecoder) {
        return reader(type, in, null, customDefaultDecoder);
    }

    /**
     * Creats a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @param customDecoders a map of custom Decoders to use in addition
     *                       or in place of the default Decoders
     * @param customDefaultDecoder a DefaultDecoder to use for processing
     *                             encoded values for which there is no decoder;
     *                             if this value is null, reading non-decodable
     *                             values throws an exception
     * @return a reader
     */
    public static Reader reader(Format type, InputStream in, Map<String, Decoder> customDecoders, DefaultDecoder customDefaultDecoder) {
        return reader(type, in, customDecoders, customDefaultDecoder,
                new MapBuilderImpl(), new ListBuilderImpl(), new ArrayBuilderImpl(), new SetBuilderImpl());
    }

    /**
     * Creats a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @param customDecoders a map of custom Decoders to use in addition
     *                       or in place of the default Decoders
     * @param customDefaultDecoder a DefaultDecoder to use for processing
     *                             encoded values for which there is no decoder;
     *                             if this value is null, reading non-decodable
     *                             values throws an exception
     * @param mapBuilder a MapBuilder to use for constructing maps while reading
     * @param listBuilder a ListBuilder to use for constructing lists while reading
     * @param arrayBuilder an ArrayBuilder to use for constructing array representations when reading
     * @param setBuilder a SetBuilder for building sets when reading
     * @return
     */
    public static Reader reader(Format type, InputStream in,
                                Map<String, Decoder> customDecoders,
                                DefaultDecoder customDefaultDecoder,
                                MapBuilder mapBuilder, ListBuilder listBuilder,
                                ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
        try {
            switch (type) {
                case JSON:
                case JSON_VERBOSE:
                    return ReaderImpl.getJsonInstance(in, customDecoders, customDefaultDecoder,
                            mapBuilder, listBuilder, arrayBuilder, setBuilder);
                case MSGPACK:
                    return ReaderImpl.getMsgpackInstance(in, customDecoders, customDefaultDecoder,
                            mapBuilder, listBuilder, arrayBuilder, setBuilder);
                default:
                    throw new IllegalArgumentException("Unknown Reader type: " + type.toString());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a string or keyword to a keyword
     * @param o A string or a keyword
     * @return a keyword
     */
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

    /**
     * Converts a string or a symbol to a symbol
     * @param o a string or a symbol
     * @return a symbol
     */
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

    /**
     * Creates a TaggedValue
     * @param tag tag string
     * @param rep value representation
     * @return a tagged value
     */
    public static TaggedValue taggedValue(String tag, Object rep) {
        return new TaggedValueImpl(tag, rep);
    }

    /**
     * Creates a TaggedValue
     * @param tag tag string
     * @param rep value representation
     * @param stringRep a string representation of the value
     * @return a tagged value
     */
    public static TaggedValue taggedValue(String tag, Object rep, String stringRep) {
        return new TaggedValueImpl(tag, rep, stringRep);
    }

    /**
     * Creates a Link
     * @param href an href value
     * @param rel a rel value
     * @return a link instance
     */
    public static Link link(String href, String rel) {
        return link(href, rel, null, null, null);
    }

    /**
     * Creates a Link
     * @param href an href value
     * @param rel a rel value
     * @return a link instance
     */
    public static Link link(URI href, String rel) {
        return link(href, rel, null, null, null);
    }

    /**
     * Creates a Link
     * @param href an href value
     * @param rel a rel value
     * @param name an optional name value
     * @param prompt an optional prompt value
     * @param render an optional render value
     * @return a link instance
     */
    public static Link link(String href, String rel, String name, String prompt, String render) {
        return link(new URIImpl(href), rel, name, prompt, render);
    }

    /**
     * Creates a Link
     * @param href an href value
     * @param rel a rel value
     * @param name an optional name value
     * @param prompt an optional prompt value
     * @param render an optional render value
     * @return a link instance
     */
    public static Link link(URI href, String rel, String name, String prompt, String render) {
        return new LinkImpl(href, rel, name, prompt, render);
    }

    /**
     * Creates a URI
     * @param uri a uri string
     * @return the URI
     */
    public static URI uri(String uri) {
        return new URIImpl(uri);
    }

    /**
     * Returns a map of tags to Decoders that is used by default
     * @return tag to Decoder map
     */
    public static Map defaultDecoders() { return ReaderImpl.defaultDecoders(); }

    /**
     * Returns a map of classes to Handlers that is used by default
     * @return class to Handler map
     */
    public static Map defaultHandlers() { return WriterImpl.defaultHandlers(); }

    /**
     * Returns the DefaultDecoder that is used by default
     * @return DefaultDecoder instance
     */
    public static DefaultDecoder defaultDefaultDecoder() { return ReaderImpl.defaultDefaultDecoder(); }

    /**
     * Returns the default MapBuilder
     * @return a MapBuilder
     */
    public static MapBuilder defaultMapBuilder() { return new MapBuilderImpl(); }

    /**
     * Returns the default ArrayBuilder
     * @return an ArrayBuilder
     */
    public static ArrayBuilder defaultArrayBuilder() { return new ArrayBuilderImpl(); }

    /**
     * Returns the default ListBuilder
     * @return a ListBuilder
     */
    public static ListBuilder defaultListBuilder() { return new ListBuilderImpl(); }

    /**
     * Returns the default SetBuilder
     * @return a SetBuilder
     */
    public static SetBuilder defaultSetBuilder() { return new SetBuilderImpl(); }
}
