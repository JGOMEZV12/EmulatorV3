package com.polar.communication.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

public class ServerPacket {

    private final ByteBuf buffer;
    private final short id;

    public ServerPacket(short id) {
        this.id = id;
        this.buffer = Unpooled.buffer();
        this.buffer.writeShort(id);
    }

    public void writeByte(int b) {
        buffer.writeByte(b);
    }

    public void writeBytes(byte[] b) {
        buffer.writeBytes(b);
    }

    public void writeString(String s) {
        if (s == null) {
            buffer.writeShort(0);
            return;
        }
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(bytes.length);
        buffer.writeBytes(bytes);
    }

    public void writeShort(int s) {
        buffer.writeShort(s);
    }

    public void writeInt(int i) {
        buffer.writeInt(i);
    }

    public void writeLong(long l) {
        buffer.writeLong(l);
    }

    public void writeBoolean(boolean b) {
        buffer.writeByte(b ? 1 : 0);
    }

    public void writeDouble(double d) {
        writeString(String.valueOf(d));
    }

    public ByteBuf getBuffer() {
        // Habbo protocol: [length (4 bytes)] [header (2 bytes)] [body]
        // Note: The header is already written to the buffer.
        int length = buffer.readableBytes();
        ByteBuf fullBuffer = Unpooled.buffer(length + 4);
        fullBuffer.writeInt(length);
        fullBuffer.writeBytes(buffer.copy());
        return fullBuffer;
    }

    public short getId() {
        return id;
    }
}
