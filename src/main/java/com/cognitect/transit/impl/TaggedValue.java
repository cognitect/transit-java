package com.cognitect.transit.impl;

public class TaggedValue {

    private final String tag;
    private final Object rep;

    public TaggedValue(String tag, Object rep) {
        this.tag = tag;
        this.rep = rep;
    }

    public String getTag() {
        return tag;
    }

    public Object getRep() {
        return rep;
    }

    @Override
    public boolean equals(Object o) {

        if(this == o)
            return true;

        if(!(o instanceof TaggedValue))
            return false;

        TaggedValue other = (TaggedValue)o;
        if(this.tag.equals(other.getTag()) && this.rep.equals(other.getRep()))
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
