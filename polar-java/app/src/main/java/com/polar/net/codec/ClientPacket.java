package com.polar.net.codec;

import java.nio.charset.StandardCharsets;

public class ClientPacket {

    private final short header;
    private final byte[] body;
    private int pointer;

    public ClientPacket(short header, byte[] body) {
        this.header = header;
        this.body = body != null ? body : new byte[0];
        this.pointer = 0;
    }

    public short getHeader() {
        return header;
    }

    public int remainingLength() {
        return body.length - pointer;
    }

    public byte[] readBytes(int n) {
        if (n > remainingLength() || n < 0) {
            n = remainingLength();
        }

        byte[] data = new byte[n];
        System.arraycopy(body, pointer, data, 0, n);
        pointer += n;
        return data;
    }

    public byte[] readFixedValue() {
        if (remainingLength() < 2) return new byte[0];
        int len = decodeInt16(readBytes(2));
        return readBytes(len);
    }

    public String popString() {
        return new String(readFixedValue(), StandardCharsets.UTF_8);
    }

    public boolean popBoolean() {
        return remainingLength() > 0 && body[pointer++] == 1;
    }

    public int popInt() {
        if (remainingLength() < 4) return 0;
        return decodeInt32(readBytes(4));
    }

    private int decodeInt32(byte[] v) {
        return ((v[0] & 0xFF) << 24) | ((v[1] & 0xFF) << 16) | ((v[2] & 0xFF) << 8) | (v[3] & 0xFF);
    }

    private short decodeInt16(byte[] v) {
        return (short) (((v[0] & 0xFF) << 8) | (v[1] & 0xFF));
    }

    @Override
    public String toString() {
        return "[" + header + "] BODY: " + new String(body, StandardCharsets.UTF_8).replace("\0", "[0]");
    }
}
