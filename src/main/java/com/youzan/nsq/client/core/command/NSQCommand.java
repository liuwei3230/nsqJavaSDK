package com.youzan.nsq.client.core.command;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Only one way of implementation
 * 
 * @author <a href="mailto:my_email@email.exmaple.com">zhaoxi (linzuxiong)</a>
 *
 * 
 */
public interface NSQCommand {

    public static final Charset ASCII = StandardCharsets.US_ASCII;
    public static final Charset UTF8 = StandardCharsets.UTF_8;
    public static final Charset DEFAULT_CHARSET = UTF8;

    /**
     * The encoding's result between UTF-8 and US-ASCII is the same underlying
     * LINE_SEPARATOR.
     */
    static final byte LINE_SEPARATOR = '\n';
    static final byte SPACE = ' ';
    static final List<byte[]> EMPTY_BODY = new ArrayList<>(0);

    // *************************************************************************
    // Normal command
    // *************************************************************************

    /**
     * @return The binary data of sending to the NSQd (broker)
     */
    byte[] getBytes();

    // *************************************************************************
    // Special command consists of header and body
    // *************************************************************************

    String getHeader();

    List<byte[]> getBody();

}
