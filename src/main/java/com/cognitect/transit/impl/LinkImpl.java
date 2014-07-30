// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Link;
import com.cognitect.transit.URI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LinkImpl implements Link {

    private static final String LINK = "link";
    private static final String IMAGE = "image";

    private static final String HREF = "href";
    private static final String REL = "rel";
    private static final String PROMPT = "prompt";
    private static final String NAME = "name";
    private static final String RENDER = "render";

    private Map<String, Object> m;

    public LinkImpl(URI href, String rel, String name, String render, String prompt) {
        Map<String, Object> m = new HashMap<String, Object>(5);
        if (href == null) throw new IllegalArgumentException("Value of href cannot be null");
        m.put(HREF, href);
        if (rel == null) throw new IllegalArgumentException("Value of rel cannot be null");
        m.put(REL, rel);
        if (name != null) m.put(NAME, name);
        if (prompt != null) m.put(PROMPT, prompt);
        if (render != null) {
            if ((render == LINK) || (render == IMAGE)) {
                m.put(RENDER, render);
            } else {
                throw new IllegalArgumentException("Value of render must be \"link\" or \"image\"");
            }
        }
        this.m = Collections.unmodifiableMap(m);
    }

    @SuppressWarnings("unchecked")
    public LinkImpl(Map m) {
        this.m = (Map<String, Object>) Collections.unmodifiableMap(m);
    }

    public Map<String, Object> toMap() { return m; }

    @Override
    public URI getHref() { return (URI) m.get(HREF); }

    @Override
    public String getRel() {
        return (String) m.get(REL);
    }

    @Override
    public String getName() {
        return (String) m.get(NAME);
    }

    @Override
    public String getPrompt() {
        return (String) m.get(PROMPT);
    }

    @Override
    public String getRender() {
        return (String) m.get(RENDER);
    }
}
