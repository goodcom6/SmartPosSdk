package com.goodcom.pos.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class PosUtils {
    private static final String TAG = "PosUtils";
    private static final int HW_SELF_CHECK_MASK_ALL = 255;

    public static int hexCharToInt(char c) {
        if (c < '0' || c > '9') {
            if (c < 'A' || c > 'F') {
                if (c < 'a' || c > 'f') {
                    throw new RuntimeException("invalid hex char '" + c + "'");
                }
                return (c - 'a') + 10;
            }
            return (c - 'A') + 10;
        }
        return c - '0';
    }

    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if (bytes == null || bytes.length == 0 || offset < 0 || dateLen <= 0 || offset >= bytes.length || bytes.length - offset < dateLen) {
            return null;
        }
        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    public static String bytesToAscii(byte[] bytes, int dateLen) {
        return bytesToAscii(bytes, 0, dateLen);
    }

    public static String bytesToAscii(byte[] bytes) {
        return bytesToAscii(bytes, 0, bytes.length);
    }

    public static String bytesToHexString(byte[] bytes, int offset, int len) {
        if (bytes == null) {
            return "null!";
        }
        StringBuilder ret = new StringBuilder(2 * len);
        for (int i = 0; i < len; i++) {
            int b = 15 & (bytes[offset + i] >> 4);
            ret.append("0123456789abcdef".charAt(b));
            int b2 = 15 & bytes[offset + i];
            ret.append("0123456789abcdef".charAt(b2));
        }
        return ret.toString();
    }

    public static String bytesToHexString(byte[] bytes, int len) {
        return bytes == null ? "null!" : bytesToHexString(bytes, 0, len);
    }

    public static String bytesToHexString(byte[] bytes) {
        return bytes == null ? "null!" : bytesToHexString(bytes, bytes.length);
    }

    public static byte[] hexStringToBytes(String s) {
        if (s == null) {
            return null;
        }
        int sz = s.length();
        try {
            byte[] ret = new byte[sz / 2];
            for (int i = 0; i < sz; i += 2) {
                ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4) | hexCharToInt(s.charAt(i + 1)));
            }
            return ret;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static byte[] shortToBytesLe(short shortValue) {
        byte[] bytes = new byte[2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((shortValue >> (i * 8)) & HW_SELF_CHECK_MASK_ALL);
        }
        return bytes;
    }

    public static byte[] shortToBytesBe(short shortValue) {
        byte[] bytes = new byte[2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[(bytes.length - i) - 1] = (byte) ((shortValue >> (i * 8)) & HW_SELF_CHECK_MASK_ALL);
        }
        return bytes;
    }

    public static byte[] intToBytesLe(int intValue) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((intValue >> (i * 8)) & HW_SELF_CHECK_MASK_ALL);
        }
        return bytes;
    }

    public static byte[] intToBytesBe(int intValue) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[(bytes.length - i) - 1] = (byte) ((intValue >> (i * 8)) & HW_SELF_CHECK_MASK_ALL);
        }
        return bytes;
    }

    public static int bytesToIntLe(byte[] bytes) {
        if (bytes == null || bytes.length > 4) {
            throw new RuntimeException("invalid arg");
        }
        int ret = 0;
        for (int i = 0; i < bytes.length; i++) {
            ret += (bytes[i] & 255) << (i * 8);
        }
        return ret;
    }

    public static int bytesToIntLe(byte[] data, int start, int end) {
        return bytesToIntLe(Arrays.copyOfRange(data, start, end));
    }

    public static int bytesToIntBe(byte[] bytes) {
        if (bytes == null || bytes.length > 4) {
            throw new RuntimeException("invalid arg");
        }
        int ret = 0;
        for (int i = 0; i < bytes.length; i++) {
            ret += (bytes[i] & 255) << (((bytes.length - i) - 1) * 8);
        }
        return ret;
    }

    public static int bytesToIntBe(byte[] data, int start, int end) {
        return bytesToIntBe(Arrays.copyOfRange(data, start, end));
    }

    public static int bytesToIntLe(byte b0, byte b1, byte b2, byte b3) {
        int ret = b0 & 255;
        return ret + ((b1 & 255) << 8) + ((b2 & 255) << 16) + ((b3 & 255) << 24);
    }

    public static int bytesToIntBe(byte b0, byte b1, byte b2, byte b3) {
        int ret = (b0 & 255) << 24;
        return ret + ((b1 & 255) << 16) + ((b2 & 255) << 8) + (b3 & 255);
    }

    public static short bytesToShortLe(byte[] bytes) {
        if (bytes == null || bytes.length > 2) {
            throw new RuntimeException("invalid arg");
        }
        short ret = 0;
        for (int i = 0; i < bytes.length; i++) {
            ret = (short) (ret + ((bytes[i] & 255) << (i * 8)));
        }
        return ret;
    }

    public static short bytesToShortLe(byte[] data, int start, int end) {
        return bytesToShortLe(Arrays.copyOfRange(data, start, end));
    }

    public static short bytesToShortBe(byte[] bytes) {
        if (bytes == null || bytes.length > 2) {
            throw new RuntimeException("invalid arg");
        }
        short ret = 0;
        for (int i = 0; i < bytes.length; i++) {
            ret = (short) (ret + ((bytes[i] & 255) << (((bytes.length - i) - 1) * 8)));
        }
        return ret;
    }

    public static short bytesToShortBe(byte[] data, int start, int end) {
        return bytesToShortBe(Arrays.copyOfRange(data, start, end));
    }

    public static short bytesToShortLe(byte b0, byte b1) {
        short ret = (short) (b0 & 255);
        return (short) (ret + ((short) ((b1 & 255) << 8)));
    }

    public static short bytesToShortBe(byte b0, byte b1) {
        short ret = (short) ((b0 & 255) << 8);
        return (short) (ret + ((short) (b1 & 255)));
    }

    public static void byteArraySetByte(byte[] bytesArray, byte setValue, int index) {
        bytesArray[index] = setValue;
    }

    public static void byteArraySetByte(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue & HW_SELF_CHECK_MASK_ALL);
    }

    public static void byteArraySetBytes(byte[] bytesArray, byte[] setValues, int index) {
        System.arraycopy(setValues, 0, bytesArray, index, setValues.length);
    }

    public static void byteArraySetWord(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 1] = (byte) ((setValue >> 8) & HW_SELF_CHECK_MASK_ALL);
    }

    public static void byteArraySetWordBe(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) ((setValue >> 8) & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 1] = (byte) (setValue & HW_SELF_CHECK_MASK_ALL);
    }

    public static void byteArraySetInt(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 1] = (byte) ((setValue >> 8) & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 2] = (byte) ((setValue >> 16) & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 3] = (byte) ((setValue >> 24) & HW_SELF_CHECK_MASK_ALL);
    }

    public static void byteArraySetIntBe(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) ((setValue >> 24) & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 1] = (byte) ((setValue >> 16) & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 2] = (byte) ((setValue >> 8) & HW_SELF_CHECK_MASK_ALL);
        bytesArray[index + 3] = (byte) (setValue & HW_SELF_CHECK_MASK_ALL);
    }

    public static void delayms(int ms) {
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
            }
        }
    }

    public static boolean isAscii(char ch) {
        if (ch <= 127) {
            return true;
        }
        return false;
    }

    public static boolean isAscii(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!isAscii(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasAsciiChar(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (isAscii(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDigitOrEnCharacter(byte c) {
        if (c < 48 || c > 57) {
            if (c < 65 || c > 90) {
                if (c >= 97 && c <= 122) {
                    return true;
                }
                return false;
            }
            return true;
        }
        return true;
    }

    public static String bcdToDecString(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((int) ((byte) ((bytes[i] & 240) >>> 4)));
            temp.append((int) ((byte) (bytes[i] & 15)));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
    }

    public static byte[] decStringToBcd(String asc) {
        int j;
        int i;
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte[] bArr = new byte[len];
        if (len >= 2) {
            len /= 2;
        }
        byte[] bbt = new byte[len];
        byte[] abt = asc.getBytes();
        for (int p = 0; p < asc.length() / 2; p++) {
            if (abt[2 * p] >= 48 && abt[2 * p] <= 57) {
                j = abt[2 * p] - 48;
            } else if (abt[2 * p] >= 97 && abt[2 * p] <= 122) {
                j = (abt[2 * p] - 97) + 10;
            } else {
                j = (abt[2 * p] - 65) + 10;
            }
            if (abt[(2 * p) + 1] >= 48 && abt[(2 * p) + 1] <= 57) {
                i = abt[(2 * p) + 1] - 48;
            } else if (abt[(2 * p) + 1] >= 97 && abt[(2 * p) + 1] <= 122) {
                i = (abt[(2 * p) + 1] - 97) + 10;
            } else {
                i = (abt[(2 * p) + 1] - 65) + 10;
            }
            int k = i;
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static String bcdToString(byte[] bcdNum) {
        return bcdToString(bcdNum, 0, bcdNum != null ? bcdNum.length : 0);
    }

    public static String bcdToString(byte[] bcdNum, int offset, int len) {
        if (len <= 0 || offset < 0 || bcdNum == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(Integer.toHexString((bcdNum[i + offset] & 240) >> 4));
            sb.append(Integer.toHexString(bcdNum[i + offset] & 15));
        }
        return sb.toString();
    }
}
