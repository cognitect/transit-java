// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.io.IOException;

public interface Reader {
    Object read() throws IOException;
}
