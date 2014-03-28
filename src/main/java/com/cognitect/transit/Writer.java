package com.cognitect.transit;

import com.cognitect.transit.impl.*;
import com.cognitect.transit.impl.handler.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;

public class Writer {

    public static final String ESC = "~";
    public static final String TAG = "#";
    public static final String SUB = "^";
    public static final String RESERVED = "`";
    public static final String ESC_TAG = "~#";

    private final Emitter e;

    private Writer(Emitter e) {

        this.e = e;
    }

    public static Map<Class, Handler> defaultHandlers() {

        Map<Class, Handler> handlers = new HashMap<Class, Handler>();

        Handler integerHandler = new NumberHandler("i");
        Handler doubleHandler = new NumberHandler("d");

        handlers.put(Boolean.class, new BooleanHandler());
        handlers.put(null, new NullHandler());
        handlers.put(String.class, new ToStringHandler("s"));
        handlers.put(Integer.class, integerHandler);
        handlers.put(Long.class, integerHandler);
        handlers.put(Short.class, integerHandler);
        handlers.put(Byte.class, integerHandler);
        handlers.put(BigInteger.class, integerHandler);
        handlers.put(Float.class, doubleHandler);
        handlers.put(Double.class, doubleHandler);
        handlers.put(HashMap.class, new MapHandler());
        handlers.put(BigDecimal.class, new ToStringHandler("f"));
        handlers.put(Character.class, new ToStringHandler("c"));
        handlers.put(Keyword.class, new ToStringHandler(":"));
        handlers.put(Symbol.class, new ToStringHandler("$"));
        handlers.put(byte[].class, new BinaryHandler());
        handlers.put(UUID.class, new UUIDHandler());
        handlers.put(URI.class, new ToStringHandler("r"));
        handlers.put(LinkedList.class, new ListHandler());
        handlers.put(AsTag.class, new AsTagHandler());
        handlers.put(URI.class, new ToStringHandler("r"));
        handlers.put(ArrayList.class, new ArrayHandler("array"));
        handlers.put(int[].class, new ArrayHandler("ints"));
        handlers.put(long[].class, new ArrayHandler("longs"));
        handlers.put(float[].class, new ArrayHandler("floats"));
        handlers.put(double[].class, new ArrayHandler("doubles"));
        handlers.put(short[].class, new ArrayHandler("shorts"));
        handlers.put(boolean[].class, new ArrayHandler("bools"));
        handlers.put(char[].class, new ArrayHandler("chars"));
        handlers.put(HashSet.class, new SetHandler());
        handlers.put(Date.class, new TimeHandler());
        handlers.put(Ratio.class, new RatioHandler());

        return handlers;
    }

    public static Writer getJsonInstance(OutputStream out, Map<Class, Handler> customHandlers) throws IOException {

        JsonFactory jf = new JsonFactory();
        JsonGenerator gen = jf.createGenerator(out);

        Map<Class, Handler> handlers = defaultHandlers();
        if(customHandlers != null) {
            Iterator<Map.Entry<Class, Handler>> i = customHandlers.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry<Class, Handler> e = i.next();
                handlers.put(e.getKey(), e.getValue());
            }
        }

        JsonEmitter emitter = new JsonEmitter(gen, handlers);

        Iterator<Handler> i = handlers.values().iterator();
        while(i.hasNext()) {
            Handler h = i.next();
            if(h instanceof TagAware)
                ((TagAware)h).setTagFinder(emitter);
        }

        return new Writer(emitter);
    }

    public void write(Object o) throws Exception {

        e.emit(o, false, new WriteCache());
    }
}
