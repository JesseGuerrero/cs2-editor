package com.example.myapp;

import java.nio.charset.StandardCharsets;

public class InputStream {

    private static final char[] CP_1252_CHARS = {
            '\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6',
            '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c',
            '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153',
            '\0', '\u017e', '\u0178'
    };

    private byte[] array;
    private int offset;

    public InputStream(byte[] data) {
        this.array = new byte[data.length];
        System.arraycopy(data, 0, this.array, 0, data.length);
        this.offset = 0;
    }

    public int readUnsignedByte() {
        return readByte() & 0xFF;
    }

    public byte readByte() {
        return array[offset++];
    }

    public int readUnsignedShort() {
        return (readUnsignedByte() << 8) + readUnsignedByte();
    }

    public int readInt() {
        return (readUnsignedByte() << 24) + (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + readUnsignedByte();
    }

    public String readString() {
        int start = offset;
        while (array[offset++] != 0) {
            // Continue until null terminator
        }
        int length = offset - start - 1;
        return length == 0 ? "" : readStringUtil(start, length);
    }

    private String readStringUtil(int start, int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int value = array[start + i] & 0xFF;
            if (value != 0) {
                if (value >= 128 && value < 160) {
                    char cp1252char = CP_1252_CHARS[value - 128];
                    if (cp1252char == 0) {
                        cp1252char = 63; // '?' character
                    }
                    sb.append(cp1252char);
                } else {
                    sb.append((char) value);
                }
            }
        }
        return sb.toString();
    }

    public String readNullString() {
        if (array[offset] == 0) {
            offset++;
            return null;
        }
        return readString();
    }

    public long readLong() {
        long l = readInt() & 0xffffffffL;
        long l1 = readInt() & 0xffffffffL;
        return (l << 32) + l1;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return array.length;
    }
}