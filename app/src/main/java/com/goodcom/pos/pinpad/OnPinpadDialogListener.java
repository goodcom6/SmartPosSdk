package com.goodcom.pos.pinpad;

public interface OnPinpadDialogListener {
	 void onError(int errCode, String msg);
	 void onConfirm(byte[] data, boolean isNonePin);
	 void onCancel();
}
