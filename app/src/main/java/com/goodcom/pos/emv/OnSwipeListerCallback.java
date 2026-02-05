package com.goodcom.pos.emv;

/**
 * @author guan
 */
public interface OnSwipeListerCallback {

    void onDisplayMsg(String msg);
    /**
    * show Confirm card number callback
    * @param cardNo card num
    */
    void onConfirmCardNo(String cardNo);
    /**
     * online callback
     * @param data emv tlv data
     */
    void onTransactionAuthRequest(byte[] data);
    /**
     * online emv end callback
     * @param code emv result
     */
    void onDisplayTransReport(int code);
    /**
     * online emv error callback
     * @param msg error message
     */
    void onError(String msg);
}
