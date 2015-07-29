// Copyright 2014 Cognitect. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cognitect.transit;


import com.cognitect.transit.SPI.ReaderSPI;
import com.cognitect.transit.impl.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
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
    public static <T> Writer<T> writer(Format type, OutputStream out) {
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
     * @param customHandlers additional WriteHandlers to use in addition
     *                       to or in place of the default WriteHandlers
     * @return a writer
     */
    public static <T> Writer<T> writer(Format type, OutputStream out, Map<Class, WriteHandler<?, ?>> customHandlers) {
        try {
            switch (type) {
                case MSGPACK:
                    return WriterFactory.getMsgpackInstance(out, customHandlers);
                case JSON:
                    return WriterFactory.getJsonInstance(out, customHandlers, false);
                case JSON_VERBOSE:
                    return WriterFactory.getJsonInstance(out, customHandlers, true);
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
        return reader(type, in, defaultDefaultReadHandler());
    }

    /**
     * Creates a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @param customHandlers a map of custom ReadHandlers to use in addition
     *                       or in place of the default ReadHandlers
     * @return a reader
     */
    public static Reader reader(Format type, InputStream in, Map<String, ReadHandler<?, ?>> customHandlers) {
        return reader(type, in, customHandlers, null);
    }



    /**
     * Creates a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @param customDefaultHandler a DefaultReadHandler to use for processing
     *                             encoded values for which there is no read handler
     * @return a reader
     */
    public static Reader reader(Format type, InputStream in, DefaultReadHandler<?> customDefaultHandler) {
        return reader(type, in, null, customDefaultHandler);
    }

    /**
     * Creates a reader instance.
     * @param type the format to read in
     * @param in the input stream to read from
     * @param customHandlers a map of custom ReadHandlers to use in addition
     *                       or in place of the default ReadHandlers
     * @param customDefaultHandler a DefaultReadHandler to use for processing
     *                             encoded values for which there is no read handler
     * @return a reader
     */
    public static Reader reader(Format type, final InputStream in,
                                final Map<String, ReadHandler<?, ?>> customHandlers,
                                final DefaultReadHandler<?> customDefaultHandler) {
        try {
            switch (type) {
                case JSON:
                case JSON_VERBOSE:
                    return ReaderFactory.getJsonInstance(in, customHandlers, customDefaultHandler);
                case MSGPACK:
                    return ReaderFactory.getMsgpackInstance(in, customHandlers, customDefaultHandler);
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
        else throw new IllegalArgumentException("Cannot make symbol from " + o.getClass().getSimpleName());
    }

    /**
     * Creates a TaggedValue
     * @param tag tag string
     * @param rep value representation
     * @return a tagged value
     */
    public static <T> TaggedValue<T> taggedValue(String tag, T rep) {
        return new TaggedValueImpl<T>(tag, rep);
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
     * Returns the DefaultReadHandler that is used by default
     * @return DefaultReadHandler instance
     */
    public static DefaultReadHandler<TaggedValue<Object>> defaultDefaultReadHandler() { return ReaderFactory.defaultDefaultHandler(); }

    /**
     * Returns a map of tags to ReadHandlers that is used by default
     * @return tag to ReadHandler map
     */
    public static Map<String, ReadHandler<?,?>> defaultReadHandlers() { return ReaderFactory.defaultHandlers(); }

    /**
     * Creates a read-only Map of String to ReadHandler containing default ReadHandlers
     * with customHandlers merged in. Use this to build the collection of read handlers
     * once, and pass it to repeated calls to TransitFactory.reader. This can be more
     * efficient
     * than repeatedly passing a map of custom handlers to TransitFactory.reader, which then
     * merges them with the default handlers and/or looks them up in
     * a cache each invocation.
     * @param customHandlers a map of custom ReadHandlers to use in addition
     *                       or in place of the default ReadHandlers
     * @return a ReadHandlerMap
     */
    public static Map<String, ReadHandler<?, ?>> readHandlerMap(Map<String, ReadHandler<?, ?>> customHandlers) {
        return new ReadHandlerMap(customHandlers);
    }

    /**
     * Returns a map of classes to Handlers that is used by default
     * @return class to Handler map
     */
    public static Map<Class, WriteHandler<?,?>> defaultWriteHandlers() { return WriterFactory.defaultHandlers(); }

    /**
     * Creates a read-only Map of String to WriteHandler containing default WriteHandlers
     * with customHandlers merged in. Use this to build the collection of write handlers
     * once, and pass it to repeated calls to TransitFactory.reader. This can be more
     * than repeatedly passing a map of custom handlers to TransitFactory.writer, which then
     * efficient
     * merges them with the default handlers and/or looks them up in
     * a cache each invocation.
     * @param customHandlers a map of custom WriteHandler to use in addition
     *                       or in place of the default WriteHandler
     * @return a WriteHandlerMap
     */
    public static Map<Class, WriteHandler<?, ?>> writeHandlerMap(Map<Class, WriteHandler<?, ?>> customHandlers) {
        return new WriteHandlerMap(customHandlers);
    }
}
