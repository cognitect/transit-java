package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WriteHandlerSet implements TagProvider {

    private Map<Class, WriteHandler<?, ?>> handlers;

    public WriteHandlerSet(Map<Class, WriteHandler<?, ?>> handlers) {
        this.handlers = handlers;
        setTagProvider(handlers);
    }

    public WriteHandlerSet getVerboseHandlerSet() {
        Map<Class, WriteHandler<?, ?>> verboseHandlers = new HashMap<Class, WriteHandler<?, ?>>(handlers.size());
        for (Map.Entry<Class, WriteHandler<?, ?>> entry : handlers.entrySet()) {
            WriteHandler<?, ?> verboseHandler = entry.getValue().getVerboseHandler();
            verboseHandlers.put(
                    entry.getKey(),
                    (verboseHandler == null) ? entry.getValue() : verboseHandler);
        }
        return new WriteHandlerSet(verboseHandlers);
    }

    private void setTagProvider(Map<Class, WriteHandler<?,?>> handlers) {
        Iterator<WriteHandler<?,?>> i = handlers.values().iterator();
        while(i.hasNext()) {
            WriteHandler h = i.next();
            if(h instanceof TagProviderAware)
                ((TagProviderAware)h).setTagProvider(this);
        }
    }

    private WriteHandler<?,?> checkBaseClasses(Class c) {
        for(Class base = c.getSuperclass(); base != Object.class; base = base.getSuperclass()) {
            WriteHandler<?, ?> h = handlers.get(base);
            if(h != null) {
                handlers.put(c, h);
                return h;
            }
        }
        return null;
    }

    private WriteHandler<?,?> checkBaseInterfaces(Class c) {
        Map<Class, WriteHandler<?,?>> possibles = new HashMap<Class,WriteHandler<?,?>>();
        for (Class base = c; base != Object.class; base = base.getSuperclass()) {
            for (Class itf : base.getInterfaces()) {
                WriteHandler<?, ?> h = handlers.get(itf);
                if (h != null) possibles.put(itf, h);
            }
        }
        switch (possibles.size()) {
            case 0: return null;
            case 1: {
                WriteHandler<?, ?> h = possibles.values().iterator().next();
                handlers.put(c, h);
                return h;
            }
            default: throw new RuntimeException("More thane one match for " + c);
        }
    }

    public WriteHandler<Object,Object> getHandler(Object o) {
        Class c = (o != null) ? o.getClass() : null;
        WriteHandler<?, ?> h = null;

        if(h == null) h = handlers.get(c);
        if(h == null) h = checkBaseClasses(c);
        if(h == null) h = checkBaseInterfaces(c);

        return (WriteHandler<Object, Object>) h;
    }

    @Override
    public String getTag(Object o) {
        WriteHandler<Object,Object> h = getHandler(o);
        if (h == null) return null;
        return h.tag(o);
    }
}
