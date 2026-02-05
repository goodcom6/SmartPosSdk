package com.goodcom.pos.utils;

public class DoCardInfoHelper {
    /**
     * Get card number string.
     *
     * @param datafield the datafield
     * @param len       the len
     * @return the string
     */
    public static String GetCardNumber(byte[] datafield, int len) {
		int i = 0;
		int ipos = 0;
		int numlen = 0;

		for (i = ipos; i < len && i < 19; i++) {
			if (datafield[i] == 0x3D || datafield[i] == 0x44) {
				break; // =
			}
			numlen++;
		}
		String Number = new String(datafield, ipos, numlen);

		return Number;
	}

    /**
     * Format card num string.
     *
     * @param cardNum the card num
     * @param hidden  the hidden
     * @return the string
     */
    public static String formatCardNum(String cardNum, boolean hidden) {
		if (cardNum == null) {
			return "";
		}
		if (cardNum.length() < 13 || cardNum.length() > 20) {
			return "";
		}

		String Number = "";
		String cardF = cardNum.substring(0, 6);
		String cardB = cardNum.substring(cardNum.length() - 4);
		String padd = "*******************";
		if (hidden) {
			Number = cardF + padd.substring(0, cardNum.length() - 10) + cardB;
		} else {
			Number = cardNum;
		}
		return Number;
	}

    /**
     * Gets mag card server code.
     *
     * @param tr2data the tr 2 data
     * @return the mag card server code
     */
    public static String getMagCardServerCode(byte[] tr2data) {

		String tr2_str = new String(tr2data);

		if (tr2_str == null) {
			return "000";
		}

		int startIndex = tr2_str.indexOf("=", 0);

		if (startIndex < 0) {
			return "000";
		}

		if (tr2_str.length() >= startIndex + 7) {
			return tr2_str.substring(startIndex + 5, startIndex + 8);
		} else {
			return "000";
		}
	}

    /**
     * Gets mag card ext date.
     *
     * @param tr2data the tr 2 data
     * @return the mag card ext date
     */
    @SuppressWarnings("unused")
	public static String getMagCardExtDate(byte[] tr2data) {

		String tr2_str = new String(tr2data);
		int startIndex = 0;
		if (tr2_str == null) {
			return "0000";
		}
		if(tr2_str.contains("=")) {
			startIndex = tr2_str.indexOf("=", 0);
		} else if (tr2_str.contains("D")) {
			startIndex = tr2_str.indexOf("D",0);
		}
		try{
			return tr2_str.substring(startIndex + 3, startIndex + 5) + tr2_str.substring(startIndex + 1, startIndex + 3);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "0000";
	}
}
