package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDHandler implements Handler {

    // TODO: UUID write support is not working because of the implementation of emitEncoded

    @Override
    public String tag(Object ignored) {
        return "u";
    }

    @Override
    public Object rep(Object o) {
        UUID uuid = (UUID)o;
        List<Long> l = new ArrayList<Long>();
        l.add(uuid.getLeastSignificantBits());
        l.add(uuid.getLeastSignificantBits());
        return l;
    }

    @Override
    public String stringRep(Object o) {
        return o.toString();
    }
}
