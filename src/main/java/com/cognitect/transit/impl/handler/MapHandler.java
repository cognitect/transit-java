// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.HandlerAware;
import com.cognitect.transit.impl.TaggedValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapHandler implements Handler, HandlerAware {

    private Handler handler;

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private boolean stringableKeys(Map m) {

        Iterator i = m.keySet().iterator();
        boolean result = true;
        while(i.hasNext()) {
            Object key = i.next();
            String tag = handler.tag(key);
            if(tag.length() > 1)
                result = false;
        }

        return result;
    }

    @Override
    public String tag(Object o) {

        Map m = (Map)o;
        if(stringableKeys(m))
            return "map";
        else
            return "cmap";
    }

    @Override
    public Object rep(Object o) {

        Map m = (Map)o;
        if(stringableKeys(m)) {
            return ((Map)o).entrySet();
        }
        else {
            List l = new ArrayList();
            Iterator<Map.Entry> i = m.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry e = i.next();
                l.add(e.getKey());
                l.add(e.getValue());
            }
            return new TaggedValue("array", l);
        }
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
