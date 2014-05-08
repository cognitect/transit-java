package com.cognitect.transit;

import java.io.IOException;

/**
 * Created by fogus on 5/8/14.
 */
public interface IReader {
    Object read() throws IOException;
}
