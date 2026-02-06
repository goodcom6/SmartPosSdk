package com.goodcom.pos.pinpad;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.goodcom.smartpossdk.PosConstants;
import com.goodcom.smartpossdk.listener.PinpadListener;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.pos.utils.DeviceTools;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.utils.BCDHelper;

public class PinpadDialog extends BaseDialog {


	private String TAG = "PinInputDialog";
	private final int PIN_SHOW = 1;
	private final int PIN_ERROR = 2;
	private final int PIN_Confirm = 3;
	private final int PIN_Cancel = 4;
	private final int PIN_PinpadShow = 5;

	private Activity mContext = null;
	private TextView key0, key1, key2, key3, key4,
			key5, key6, key7, key8, key9;
	private TextView clean, confirm, password;
	private TextView close;
	private ConstraintLayout keyBoard;
	private int delayTimeout = 150;
	private boolean isInputOnline = true;
	private OnPinpadDialogListener pinpadDialogListener;

	public  PinpadDialog(Activity context, String cardno, int type,OnPinpadDialogListener listener){
		super(context, R.layout.dialog_pay_password, Gravity.BOTTOM, false);
		init(context,cardno,1,1,type,60,listener);
	}
	
	public PinpadDialog(Activity context, String cardno, int area, int index, int type, int timeout,OnPinpadDialogListener listener) {
		super(context, R.layout.dialog_pay_password, Gravity.BOTTOM, false);
		init(context, cardno, area, index, type, timeout, listener);
	}

	private void init(Activity context, String cardno, int area, int index, int type, int timeout,OnPinpadDialogListener listener){
		this.mContext = context;
		initview();
		pinpadDialogListener = listener;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initPinPad(cardno,area,index,type,timeout);
			}
		},200);
	}

	private void initPinPad(String cardno, int area, int index, int type, int timeout) {
		try {
			GcSmartPosUtils.getInstance().setPinpadMode(MainApp.getInstance().getApplicationContext(),
					PosConstants.PinPad.PinpadMode.MODE_RANDOM);
			GcSmartPosUtils.getInstance().setOnPinInputListener(MainApp.getInstance().getApplicationContext(), new onPinpadListener());
			if(type == 1) {
				GcSmartPosUtils.getInstance().inputOnlinePinDukpt(MainApp.getInstance().getApplicationContext(),index, PosConstants.PinPad.DukptPinBlockMode.FORMAT0_ADD_KSN,timeout,cardno, new byte[]{0, 4, 6, 12});
			} else if(type == 2){
				GcSmartPosUtils.getInstance().inputOnlinePin(MainApp.getInstance().getApplicationContext(),area,index, timeout,cardno, new byte[]{0,4,5,6,7,8,9,10,11,12});
			}else if(type == 3){
				GcSmartPosUtils.getInstance().inputOfflinePin(MainApp.getInstance().getApplicationContext(),0,new byte[]{0,4,5,6,7,8,9,10,11,12},timeout);
			}else if(type == 4){
				GcSmartPosUtils.getInstance().inputOfflinePin(MainApp.getInstance().getApplicationContext(),1,new byte[]{0,4,5,6,7,8,9,10,11,12},timeout);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initview() {
		key0 = findViewById(R.id.tv_0);
		key1 = findViewById(R.id.tv_1);
		key2 = findViewById(R.id.tv_2);
		key3 = findViewById(R.id.tv_3);
		key4 = findViewById(R.id.tv_4);
		key5 = findViewById(R.id.tv_5);
		key6 = findViewById(R.id.tv_6);
		key7 = findViewById(R.id.tv_7);
		key8 = findViewById(R.id.tv_8);
		key9 = findViewById(R.id.tv_9);
		clean = findViewById(R.id.tv_clear);
		confirm = findViewById(R.id.tv_sure);
		close = findViewById(R.id.tv_cancel);
		password = findViewById(R.id.tv_pwd);
		keyBoard = findViewById(R.id.layout_password_keyboard);
	}
	private Handler pinpad_model = new Handler() {
		@Override
		public void handleMessage(@NonNull Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			switch (msg.what) {
				case PIN_SHOW:
					int len = bundle.getInt("len");
					String str = "";
					for (int i = 0; i < len; i++) {
						str += " " + "*";
					}
					password.setText(str);
					break;
				case PIN_ERROR:
					int errorCode = bundle.getInt("errorCode");
					pinpadDialogListener.onError(errorCode,null);
					dismiss();
					break;
				case PIN_Confirm:
					byte[] pinBlock = bundle.getByteArray("data");
					boolean isNonePin = bundle.getBoolean("isNonePin");
					pinpadDialogListener.onConfirm(pinBlock, isNonePin);
					dismiss();

					break;
				case PIN_Cancel:
					pinpadDialogListener.onCancel();
					dismiss();
					break;
				case PIN_PinpadShow:

					byte[] bytes = bundle.getByteArray("data");
					if (bytes != null) {
						keyBoard.setVisibility(View.VISIBLE);
						setPWLayout(bytes);
						getKeyLayout13();
					}else{
						keyBoard.setVisibility(View.GONE);
					}
					break;
			}
		}
	};

	private class onPinpadListener extends PinpadListener.Stub{

		@Override
		public void onInput(int len, int key) throws RemoteException {
			Log.e(TAG,"onInput len:"+len);
			Message message = new Message();
			message.what = PIN_SHOW;
			Bundle bundle = new Bundle();
			bundle.putInt("len", len);
			bundle.putInt("key", key);
			message.setData(bundle);
			pinpad_model.sendMessage(message);
		}

		@Override
		public void onError(int errorCode) throws RemoteException {
			Log.e(TAG,"onError errorCode:"+errorCode);
			Message message = new Message();
			message.what = PIN_ERROR;
			Bundle bundle = new Bundle();
			bundle.putInt("errorCode", errorCode);
			message.setData(bundle);
			pinpad_model.sendMessage(message);
		}

		@Override
		public void onConfirm(byte[] data, boolean isNonePin) throws RemoteException {
			Log.e(TAG,"onConfirm isNonePin:"+isNonePin);
			if(data != null) {
				Log.e(TAG, "onConfirm data:" + new String(data));
				Log.e(TAG, "onConfirm data:" + BCDHelper.bcdToString(data,0,data.length));
			}
			Message message = new Message();
			message.what = PIN_Confirm;
			Bundle bundle = new Bundle();
			bundle.putByteArray("data",data);
			bundle.putBoolean("isNonePin",isNonePin);
			message.setData(bundle);
			pinpad_model.sendMessage(message);
		}

		@Override
		public void onCancel() throws RemoteException {
			Log.e(TAG,"onCancel");
			Message message = new Message();
			message.what = PIN_Cancel;
			Bundle bundle = new Bundle();
			message.setData(bundle);
			pinpad_model.sendMessage(message);
		}

		@Override
		public void onPinpadShow(byte[] data) throws RemoteException {
			if(data != null) {
				Log.e(TAG, "onPinpadShow data:" + BCDHelper.bcdToString(data,0,data.length));
			}
			Message message = new Message();
			message.what = PIN_PinpadShow;
			Bundle bundle = new Bundle();
			bundle.putByteArray("data",data);
			message.setData(bundle);
			pinpad_model.sendMessage(message);
		}
	}

	private void setPWLayout(byte[] keys) {
		Log.e(TAG,"setPWLayout keys:"+BCDHelper.bcdToString(keys,0,keys.length));
		key1.setText(String.valueOf(keys[0] - 0x30));

		key2.setText(String.valueOf(keys[1] - 0x30));

		key3.setText(String.valueOf(keys[2] - 0x30));


		key4.setText(String.valueOf(keys[3] - 0x30));

		key5.setText(String.valueOf(keys[4] - 0x30));

		key6.setText(String.valueOf(keys[5] - 0x30));


		key7.setText(String.valueOf(keys[6] - 0x30));

		key8.setText(String.valueOf(keys[7] - 0x30));

		key9.setText(String.valueOf(keys[8] - 0x30));


		key0.setText(String.valueOf(keys[10] - 0x30));
	}

	public byte[] getKeyLayout13(){
		int pos = 0;
		Log.e(TAG, "getKeyLayout");
		byte[] keylayout = new byte[104];

		pos = addToByteArray(getWidgetPosition(key1), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key2), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key3), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key4), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key5), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key6), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key7), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key8), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key9), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(close), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(key0), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(confirm), keylayout, pos);
		pos = addToByteArray(getWidgetPosition(clean), keylayout, pos);

		Log.e(TAG, "getKeyLayout = " + BCDHelper.hex2DebugHexString(keylayout, keylayout.length));
		Log.e(TAG,"getKeyLayout length:"+keylayout.length);

		try {
			GcSmartPosUtils.getInstance().setPinpadLayout(MainApp.getInstance().getApplicationContext(), keylayout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keylayout;
	}

	private int addToByteArray(byte[] src, byte[] dest, int position) {
		System.arraycopy(src, 0, dest, position, src.length);
		return position += src.length;
	}

	private byte[] getWidgetPosition(View widget) {
		int[] location = new int[2];
		widget.getLocationOnScreen(location);
		int leftx, lefty, rightx, righty;
		leftx = location[0];
		lefty = location[1];
		rightx = location[0] + widget.getWidth();
		righty = location[1] + widget.getHeight();
		byte[] pos = new byte[8];
		// 0,768 0x0000 0x02fc
		// 0x00,0x00,0x02,0xfc

		byte[] tmp = BCDHelper.intToBytes2(leftx);
		byte[] tmp1 = BCDHelper.intToBytes2(lefty);
		byte[] tmp2 = BCDHelper.intToBytes2(rightx);
		byte[] tmp3 = BCDHelper.intToBytes2(righty);

		pos[0] = tmp[2];

		pos[1] = tmp[3];

		pos[2] = tmp1[2];

		pos[3] = tmp1[3];

		pos[4] = tmp2[2];

		pos[5] = tmp2[3];

		pos[6] = tmp3[2];

		pos[7] = tmp3[3];
		return pos;
	}

	@Override
	public void dismiss() {
		Log.e(TAG,"dismiss");
		super.dismiss();
		try {
//			MainApp.getInstance().mDeviceInfoOpt.disableKeyCode(KeyEvent.KEYCODE_POWER, 0);
//			mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			DeviceTools.closeKioskMode(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void show() {
		Log.e(TAG,"show");
		super.show();
		try {
			DeviceTools.openKioskMode(mContext);
//			MainApp.getInstance().mDeviceInfoOpt.disableKeyCode(KeyEvent.KEYCODE_POWER, 1);
//			mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
