// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.TaggedValue;

public class TaggedValueImpl implements TaggedValue {

    private final String tag;
    private final Object rep;
    private final String stringRep;

    public TaggedValueImpl(String tag, Object rep) {
        this.tag = tag;
        this.rep = rep;
        this.stringRep = null;
    }

    public TaggedValueImpl(String tag, Object rep, String stringRep) {
        this.tag = tag;
        this.rep = rep;
        this.stringRep = stringRep;
    }

    public String getTag() {
        return tag;
    }

    public Object getRep() {
        return rep;
    }

    public String getStringRep() { return stringRep; }

    @Override
    public boolean equals(Object o) {

        if(this == o)
            return true;

        if(!(o instanceof TaggedValueImpl))
            return false;

        TaggedValueImpl other = (TaggedValueImpl)o;
        if(this.tag.equals(other.getTag()) &&
                this.rep.equals(other.getRep()) &&
                ((this.stringRep == null && other.stringRep == null) ||
                        (this.stringRep.equals(other.stringRep))))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result * tag.hashCode();
        result = 31 * result * rep.hashCode();

        return result;
    }
}
