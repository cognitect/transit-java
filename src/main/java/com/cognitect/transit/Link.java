// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public interface Link {
    public String href();
    public String rel();
    public String name();
    public String prompt();
    public String render();
}
