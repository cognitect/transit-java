package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import com.cognitect.transit.impl.handler.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonWriter extends AbstractWriter implements Writer {

    private final OutputStream out;
    private final Emitter e;

    private final Map<Class, Handler> handlers = new HashMap<Class, Handler>();

    public JsonWriter(OutputStream out) throws IOException {

        this.out = out;
        JsonFactory jf = new JsonFactory();
        JsonGenerator gen = jf.createGenerator(out);

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

        e = new JsonEmitter(gen, handlers);
    }

    public JsonWriter setCustomHandler(Class c, Handler handler) {

        handlers.put(c, handler);
        return this;
    }

    @Override
    public void write(Object o) throws Exception {

        e.emit(o, false, null);
    }
}
