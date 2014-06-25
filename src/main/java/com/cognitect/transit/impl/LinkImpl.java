// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Link;

import java.util.HashMap;
import java.util.Map;

public class LinkImpl implements Link {

    public Map<String, String> m;

    private static String HREF = "href";
    private static String REL = "rel";
    private static String NAME = "name";
    private static String PROMPT = "prompt";
    private static String RENDER = "render";
    private static String LINK = "link";
    private static String IMAGE = "image";

    public LinkImpl(String href, String rel, String name, String prompt, String render) {
        m = new HashMap();
        if (href != null) {
            m.put(HREF, href);
        } else {
            throw new IllegalArgumentException("href cannot be null");
        }
        if (rel != null) {
            m.put(REL, rel);
        } else {
            throw new IllegalArgumentException("rel cannot be null");
        }
        if (name != null) {
            m.put(NAME, name);
        }
        if (prompt != null) {
            m.put(PROMPT, prompt);
        }
        if (render != null) {
            render = render.toLowerCase();
            if ((render != LINK) && (render != IMAGE)) {
                throw new IllegalArgumentException("render must be either \"link\" or \"image\"");
            } else {
                m.put(RENDER, render);
            }
        }
    }

    public LinkImpl(Map m) {
        this.m = m;
    }

    @Override
    public String href() {
        return m.get(HREF);
    }

    @Override
    public String rel() {
        return m.get(REL);
    }

    @Override
    public String name() {
        return m.get(NAME);
    }

    @Override
    public String prompt() {
        return m.get(PROMPT);
    }

    @Override
    public String render() {
        return m.get(RENDER);
    }
}
