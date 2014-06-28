// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Link;

import java.util.ArrayList;
import java.util.List;

public class LinkImpl implements Link {

    private static String LINK = "link";
    private static String IMAGE = "image";

    private String href;
    private String rel;
    private String name;
    private String render;
    private String prompt;

    public LinkImpl(String href, String rel, String name, String render, String prompt) {
        this.href = href;
        this.rel = rel;
        this.name = name;
        this.render = render;
        this.prompt = prompt;
    }

    public LinkImpl(List l) {
        this.href = (String) l.get(0);
        this.rel = (String) l.get(1);
        this.name = (String) l.get(2);
        this.render = (String) l.get(3);
        this.prompt = (String) l.get(4);
    }

    public List toList() {
        List l = new ArrayList(5);
        l.add(href);
        l.add(rel);
        l.add(name);
        l.add(render);
        l.add(prompt);
        return l;
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public String getRel() {
        return rel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrompt() {
        return prompt;
    }

    @Override
    public String getRender() {
        return render;
    }
}
