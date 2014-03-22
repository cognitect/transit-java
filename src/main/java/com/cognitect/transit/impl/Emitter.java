package com.cognitect.transit.impl;

import com.cognitect.transit.Tag;

public interface Emitter {

    void emit(Object o, boolean asMapKey, WriteCache cache) throws Exception;
    void emitString(char prefix, Tag tag, String s, boolean asMapKey, WriteCache cache) throws Exception;
    void emitBoolean(Boolean b, boolean asMapKey, WriteCache cache) throws Exception;
}
