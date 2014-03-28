package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.AsTag;
import com.cognitect.transit.impl.TagAware;
import com.cognitect.transit.impl.TagFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapHandler implements Handler, TagAware {

    private TagFinder tagFinder;

    @Override
    public void setTagFinder(TagFinder tf) {
        this.tagFinder = tf;
    }

    private boolean stringableKeys(Map m) {

        Iterator i = m.keySet().iterator();
        boolean result = true;
        while(i.hasNext()) {
            Object key = i.next();
            String tag = tagFinder.getTag(key);
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
            return new AsTag("array", l, null);
        }
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
