package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;
import com.cognitect.transit.Writer;
import com.cognitect.transit.impl.AbstractWriter;
import com.cognitect.transit.impl.Emitter;
import com.cognitect.transit.impl.WriteCache;
import com.fasterxml.jackson.core.JsonGenerator;

import java.util.Map;

public class JsonEmitter implements Emitter {

    private final JsonGenerator gen;
    private final Map<Class, Handler> handlers;

    public JsonEmitter(JsonGenerator gen, Map<Class, Handler> handlers) {

        this.gen = gen;
        this.handlers = handlers;
    }

    @Override
    public void emit(Object o, boolean asMapKey, WriteCache cache) throws Exception {

        Handler h = handlers.get(o.getClass());
        if(h != null) {
            switch(h.tag(o)) {
                case BOOLEAN: emitBoolean((Boolean)h.rep(o), asMapKey, cache);
                default: break;
            }
            gen.flush();
        }
        else {
            throw new Exception("No handler for class " + o.getClass());
        }
    }

    @Override
    public void emitString(char prefix, Tag tag, String s, boolean asMapKey, WriteCache cache) throws Exception {

        // TODO: use cache
        String outString = Character.toString(prefix) + tag + s;
        if(asMapKey)
            gen.writeFieldName(outString);
        else
            gen.writeString(outString);
    }

    @Override
    public void emitBoolean(Boolean b, boolean asMapKey, WriteCache cache) throws Exception {

        if(asMapKey) {
            emitString(Writer.ESC, Tag.BOOLEAN, b.toString(), asMapKey, cache);
        }
        else {
            gen.writeBoolean(b);
        }
    }
}
