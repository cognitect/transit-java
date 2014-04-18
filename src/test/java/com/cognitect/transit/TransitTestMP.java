package com.cognitect.transit;

import com.cognitect.transit.impl.AbstractParser;
import com.cognitect.transit.impl.JsonParser;
import com.cognitect.transit.impl.ReadCache;
import com.cognitect.transit.impl.WriteCache;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.codec.binary.Base64;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransitTestMP extends TestCase {

    public TransitTestMP(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TransitTestMP.class);
    }

    // Reading

    public Reader readerOf(String s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack msgpack = new MessagePack();
        Packer packer = msgpack.createPacker(out);
        packer.write(s);

        InputStream in = new ByteArrayInputStream(out.toByteArray());
        return Reader.getMsgpackInstance(in, null);
    }

    public void testReadString() throws IOException {

        assertEquals("foo", readerOf("foo").read());
        assertEquals("~foo", readerOf("~~foo").read());
        assertEquals("`foo", readerOf("~`foo").read());
        assertEquals("~#foo", readerOf("~#foo").read());
        assertEquals("^foo", readerOf("~^foo").read());
    }
}
