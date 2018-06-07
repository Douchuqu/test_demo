package com.hc.wxstcdemo.utils;

public class ScaleUtils {

    /**
     * ʮ������ת���ַ���
     *
     * @param String str Byte�ַ���(Byte֮���޷ָ��� ��:[616C6B])
     * @return String ��Ӧ���ַ���
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytesת����ʮ�������ַ���
     *
     * @param byte[] b byte����
     * @return String ÿ��Byteֵ֮��ո�ָ�
     */
    public static String byte2HexStr(byte[] b) {
        if (b == null)
            return "";
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytesת����ʮ�������ַ���
     *
     * @param byte[] b byte����
     * @return String ÿ��Byteֵ���ŵ�
     */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * bytes�ַ���ת��ΪByteֵ
     *
     * @param String src Byte�ַ�����ÿ��Byte֮��û�зָ���
     * @return byte[]
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // ��λһ�飬��ʾһ���ֽ�,��������ʾ��16�����ַ�������ԭ��һ���ֽ�
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * String���ַ���ת����unicode��String
     *
     * @param String strText ȫ���ַ���
     * @return String ÿ��unicode֮���޷ָ���
     * @throws Exception
     */
    public static String strToUnicode(String strText)
            throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // ��λ��ǰ�油00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * unicode��Stringת����String���ַ���
     *
     * @param String hex 16����ֵ�ַ��� ��һ��unicodeΪ2byte��
     * @return String ȫ���ַ���
     */
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // ��λ��Ҫ����00��ת
            String s1 = s.substring(2, 4) + "00";
            // ��λֱ��ת
            String s2 = s.substring(4);
            // ��16���Ƶ�stringתΪint
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // ��intת��Ϊ�ַ�
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * �ַ���������
     */
    public static String Str_reverse(String str) {
        StringBuffer sb = new StringBuffer(str);
        return sb.reverse().toString();
    }

}
