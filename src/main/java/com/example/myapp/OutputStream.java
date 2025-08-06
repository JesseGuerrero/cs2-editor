package com.example.myapp;

import java.util.Arrays;

public class OutputStream {

    private byte[] array;
    private int offset;

    public OutputStream() {
        this(16);
    }

    public OutputStream(int size) {
        this.array = new byte[size];
        this.offset = 0;
    }

    public void writeByte(int value) {
        int position = offset++;
        checkCapacity(position);
        array[position] = (byte) value;
    }

    public void writeBytes(byte[] value) {
        checkCapacity(offset + value.length);
        System.arraycopy(value, 0, array, offset, value.length);
        offset += value.length;
    }

    public void writeShort(int value) {
        writeByte(value >> 8);
        writeByte(value);
    }

    public void writeInt(int value) {
        writeByte(value >> 24);
        writeByte(value >> 16);
        writeByte(value >> 8);
        writeByte(value);
    }

    public void writeLong(long value) {
        writeByte((int)(value >> 56));
        writeByte((int)(value >> 48));
        writeByte((int)(value >> 40));
        writeByte((int)(value >> 32));
        writeByte((int)(value >> 24));
        writeByte((int)(value >> 16));
        writeByte((int)(value >> 8));
        writeByte((int)value);
    }

    public void writeString(String value) {
        int first = value.indexOf(0);
        if (first >= 0) {
            throw new IllegalArgumentException("String contains null character");
        }
        checkCapacity(offset + value.length() + 1);
        offset += writeStringUtil(value, 0, value.length());
        array[offset++] = 0;
    }

    private int writeStringUtil(String value, int start, int end) {
        int length = end - start;

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if ((c > 0 && c < 128) || (c >= 160 && c <= 255)) {
                array[i + offset] = (byte) c;
            } else if (c == 8364) { // €
                array[i + offset] = -128;
            } else if (c == 8218) { // ‚
                array[i + offset] = -126;
            } else if (c == 402) { // ƒ
                array[i + offset] = -125;
            } else if (c == 8222) { // „
                array[i + offset] = -124;
            } else if (c == 8230) { // …
                array[i + offset] = -123;
            } else if (c == 8224) { // †
                array[i + offset] = -122;
            } else if (c == 8225) { // ‡
                array[i + offset] = -121;
            } else if (c == 710) { // ˆ
                array[i + offset] = -120;
            } else if (c == 8240) { // ‰
                array[i + offset] = -119;
            } else if (c == 352) { // Š
                array[i + offset] = -118;
            } else if (c == 8249) { // ‹
                array[i + offset] = -117;
            } else if (c == 338) { // Œ
                array[i + offset] = -116;
            } else if (c == 381) { // Ž
                array[i + offset] = -114;
            } else if (c == 8216) { // '
                array[i + offset] = -111;
            } else if (c == 8217) { // '
                array[i + offset] = -110;
            } else if (c == 8220) { // "
                array[i + offset] = -109;
            } else if (c == 8221) { // "
                array[i + offset] = -108;
            } else if (c == 8226) { // •
                array[i + offset] = -107;
            } else if (c == 8211) { // –
                array[i + offset] = -106;
            } else if (c == 8212) { // —
                array[i + offset] = -105;
            } else if (c == 732) { // ˜
                array[i + offset] = -104;
            } else if (c == 8482) { // ™
                array[i + offset] = -103;
            } else if (c == 353) { // š
                array[i + offset] = -102;
            } else if (c == 8250) { // ›
                array[i + offset] = -101;
            } else if (c == 339) { // œ
                array[i + offset] = -100;
            } else if (c == 382) { // ž
                array[i + offset] = -98;
            } else if (c == 376) { // Ÿ
                array[i + offset] = -97;
            } else {
                array[i + offset] = 63; // '?' character
            }
        }
        return length;
    }

    private void checkCapacity(int position) {
        if (position >= array.length) {
            byte[] newArr = new byte[position + 16];
            System.arraycopy(array, 0, newArr, 0, array.length);
            array = newArr;
        }
    }

    public byte[] toArray() {
        return Arrays.copyOf(array, offset);
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return array.length;
    }
}