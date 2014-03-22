package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Writer;
import com.cognitect.transit.impl.decode.JsonEmitter;
import com.cognitect.transit.impl.handler.BooleanHandler;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonWriter extends AbstractWriter implements Writer {

    private final OutputStream out;
    private final Emitter e;

    private final Map<Class, Handler> handlers = new HashMap<Class, Handler>();

    public JsonWriter(OutputStream out) throws IOException {

        this.out = out;
        JsonFactory jf = new JsonFactory();
        JsonGenerator gen = jf.createGenerator(out);

        handlers.put(Boolean.class, new BooleanHandler());

        this.e = new JsonEmitter(gen, handlers);
    }

    @Override
    public void write(Object o) throws Exception {

        e.emit(o, false, null);
    }
}
