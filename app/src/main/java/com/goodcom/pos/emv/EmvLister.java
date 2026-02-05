package com.goodcom.pos.emv;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.goodcom.administrator.generalprinter.constant.PosConstants;
import com.goodcom.administrator.generalprinter.entity.PinpadResult;
import com.goodcom.administrator.generalprinter.listener.PosEmvCoreListener;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.pos.dialog.EnterDialog;
import com.goodcom.pos.pinpad.OnPinpadDialogListener;
import com.goodcom.pos.pinpad.PinpadDialog;
import com.goodcom.pos.utils.DoCardInfoHelper;
import com.goodcom.pos.utils.TlvData;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.utils.BCDHelper;
import com.goodcom.smartpossdk.utils.LogUtil;
import com.goodcom.smartpossdk.utils.StringUtils;

import java.util.List;

/**
 * @author guan
 */
public class EmvLister extends PosEmvCoreListener.Stub {
    private static final String TAG = "EmvNewLister";

    private final Activity mActivity;
    private String mCardNo = "";
    private final OnSwipeListerCallback mOnEmvListerCallback;
    private int cardType;
    private boolean isAlreadyOnline = false;
    private int pinType;

    public EmvLister(Activity activity, OnSwipeListerCallback onEmvListerCallback) {
        this.mActivity = activity;
        this.mOnEmvListerCallback = onEmvListerCallback;
    }

    @Override
    public void onEmvProcess(int type, Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onEmvProcess: " + type);
        Log.e(TAG, "onEmvProcess Detect Card");
        cardType = type;
        isAlreadyOnline = false;

        switch (type) {
            case PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACT:
                MainApp.getInstance().getTransData().setEnterMode(TransData.EnterMode.INSERT);
                LogUtil.se(getClass(), "DEVICE_CONTACT ");
                break;
            case PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACTLESS:
                MainApp.getInstance().getTransData().setEnterMode(TransData.EnterMode.PICC);
                LogUtil.se(getClass(), "DEVICE_CONTACTLESS:");
                break;
            case PosConstants.EMV.EmvSupportCardType.DEVICE_MAGSTRIPE:
                MainApp.getInstance().getTransData().setEnterMode(TransData.EnterMode.SWIPE);
                LogUtil.se(getClass(), "DEVICE_MAGSTRIPE:");

                byte[] buff;
                buff = bundle.getByteArray(PosConstants.EMV.EmvCardInfoConstraints.TRACK1);
                if (buff != null && buff.length > 0) {
                    LogUtil.se(getClass(), "TRACK1:" + new String(buff));
                    LogUtil.se(getClass(), "TRACK1:" + BCDHelper.bcdToString(buff, 0, buff.length));
                    MainApp.getInstance().getTransData().setTrack1(new String(buff));
                }
                buff = bundle.getByteArray(PosConstants.EMV.EmvCardInfoConstraints.TRACK2);
                if (buff != null && buff.length > 0) {
                    LogUtil.se(getClass(), "TRACK2:" + new String(buff));
                    LogUtil.se(getClass(), "TRACK2:" + BCDHelper.bcdToString(buff, 0, buff.length));
                    MainApp.getInstance().getTransData().setTrack2(new String(buff));

                    MainApp.getInstance().getTransData().setPan(DoCardInfoHelper.GetCardNumber(buff, buff.length));

                    MainApp.getInstance().getTransData().setExpDate(DoCardInfoHelper.getMagCardExtDate(buff));

                    String serverCode = DoCardInfoHelper.getMagCardServerCode(buff);
                    if (!StringUtils.isEmpty(serverCode)) {
                        MainApp.getInstance().getTransData().setCardSerialNo(serverCode);
                    }

                }
                buff = bundle.getByteArray(PosConstants.EMV.EmvCardInfoConstraints.TRACK3);
                if (buff != null && buff.length > 0) {
                    LogUtil.se(getClass(), "TRACK3:" + new String(buff));
                    LogUtil.se(getClass(), "TRACK3:" + BCDHelper.bcdToString(buff, 0, buff.length));
                    MainApp.getInstance().getTransData().setTrack3(new String(buff));
                }

                String pan = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5A");
                LogUtil.se(getClass(), "pan:" + pan);

                String expData = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F24");
                LogUtil.se(getClass(), "expData:" + expData);

                String serviceCode = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F30");
                LogUtil.se(getClass(), "serviceCode:" + serviceCode);

                String track2 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "57");

                LogUtil.se(getClass(), "track2:" + track2);

                break;
            default:
                mOnEmvListerCallback.onError("Emv Error Code:" + String.valueOf(type));
                break;
        }


    }

    @Override
    public void onSelectApplication(List<String> list, boolean isFirstSelect) throws RemoteException {
        LogUtil.se(getClass(), "onSelectApplication: ");
        GcSmartPosUtils.getInstance().onSetSelectResponse(MainApp.getInstance().getApplicationContext(), 0);
    }

    @Override
    public void onConfirmCardInfo(int mode, Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onConfirmCardInfo: " + mode + "bundle" + bundle);

        String track2 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "57");

        LogUtil.se(getClass(), "track2:" + track2);

        Bundle outBundle = new Bundle();
        if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_CARD_NUMBER) {
            mCardNo = bundle.getString(PosConstants.EMV.EmvCardInfoConstraints.CARD);
            mOnEmvListerCallback.onConfirmCardNo(mCardNo);
        }
        if (TextUtils.isEmpty(mCardNo)) {
            mCardNo = null;
            LogUtil.se(getClass(), "onConfirmCardInfo: card number is null.");
            GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);

            return;
        }

        if (cardType != PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACT) {
            outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
            GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
            return;
        }

        mActivity.runOnUiThread(() -> {
            new EnterDialog(mActivity).showConfirmDialog("please confirm cardno!", mCardNo, new EnterDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    try {
                        outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
                        GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                    mOnEmvListerCallback.onError(mActivity.getString(R.string.error_cancel_trans));
                    outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, false);
                    GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
                }
            });
        });
    }

    public void onConfirmGeneralInfo(int mode, Bundle bundle) throws RemoteException {
        Log.e(TAG, "onConfirmGeneralInfo mode: " + mode);
        Bundle outBundle = new Bundle();
        Log.e(TAG, "onConfirmGeneralInfo: " + mode);
        if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_AMOUNT_CONFIG) {
            outBundle.putString(PosConstants.EMV.EmvCardInfoConstraints.OUT_AMOUNT, "11");
            outBundle.putString(PosConstants.EMV.EmvCardInfoConstraints.OUT_AMOUNT_OTHER, "22");
        } else if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_TRY_OTHER_APPLICATION) {
            outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
        } else if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_ISSUER_REFERRAL) {
            outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
        } else if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_AID_NAME) {
            String aid = bundle.getString(PosConstants.EMV.EmvCardInfoConstraints.DATA);
            Log.e(TAG, "onConfirmGeneralInfo: " + aid);
        }
        String tags[] = new String[]{"4F", "9F06", "50", "82"};
        String tlvs = GcSmartPosUtils.getInstance().EmvGetTlvDataList(MainApp.getInstance().getApplicationContext(), tags);
        if (tlvs != null) {
            Log.e(TAG, "tlvs: " + tlvs);
        } else {
            Log.e(TAG, "tlvs is null ");
        }
        GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
    }

    @Override
    public void onExchangeData(int mode) throws RemoteException {
        Log.e(TAG, "onExchangeData mode: " + mode);

//        byte[] data;
//        switch (mode) {
//            case PosConstants.EMV.EmvCallbackCommandType.CMD_GPO_FILTER:
//                data = HexUtil.parseHex("9A03230517");
//                break;
//            case PosConstants.EMV.EmvCallbackCommandType.CMD_READ_RECORD_FILTER:
//                data = HexUtil.parseHex("9F3501229C0101");
//                break;
//            case PosConstants.EMV.EmvCallbackCommandType.CMD_SECOND_GAC_FILTER:
//                data = HexUtil.parseHex("9F2103152826");
//                break;
//            default:
//                data = new byte[0];
//                break;
//        }
//        GcSmartPosUtils.getInstance().EmvSetTlvData(data);

    }

    @Override
    public void onKernelType(int type) throws RemoteException {
        LogUtil.se(getClass(), "onKernelType: " + type);

    }

    @Override
    public void onSecondTapCard() throws RemoteException {
        LogUtil.se(getClass(), "onSecondTapCard: ");
    }

    @Override
    public void onRequestInputPin(Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onRequestInputPin: ");


        Bundle mBundle = new Bundle();
//        if (Build.MODEL.toUpperCase().equalsIgnoreCase("P3000")) {
//            mBundle.putInt(EmvPinConstraints.OUT_PIN_VERIFY_RESULT, EmvPinConstraints.VERIFY_ERROR);
//            GcSmartPosUtils.getInstance().onSetPinResponse(MainApp.getInstance().getApplicationContext(),mBundle);
//            return;
//        }
        Log.e(TAG, "onRequestInputPin: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 55) {
            byte[] data = "4BDF92D68182929C".getBytes();
            if (data != null) {
                LogUtil.se(getClass(), "input online pin onConfirm data:" + new String(data));
                MainApp.getInstance().getTransData().setPin(new String(data));
                MainApp.getInstance().getTransData().setHasPin(true);
            }
            try {
                mBundle.putInt(PosConstants.EMV.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, PosConstants.EMV.EmvPinConstraints.VERIFY_SUCCESS);
                mBundle.putByteArray(PosConstants.EMV.EmvPinConstraints.OUT_PIN_BLOCK, data);
                GcSmartPosUtils.getInstance().onSetPinResponse(MainApp.getInstance().getApplicationContext(), mBundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        pinType = bundle.getInt(PosConstants.EMV.EmvPinConstraints.PIN_TYPE);
        switch (pinType) {
            case PosConstants.EMV.EmvPinType.PIN_PLAIN_PIN:
                Log.e(TAG, "onRequestInputPin: " + "online pin");
                pinType = 3;
                break;
            case PosConstants.EMV.EmvPinType.PIN_ENCIPHER_PIN:
                Log.e(TAG, "onRequestInputPin: " + "encipher pin");
                pinType = 4;
                break;
            case PosConstants.EMV.EmvPinType.PIN_ONLINE_PIN:
            default:
                Log.e(TAG, "onRequestInputPin: " + "plain pin");
                pinType = 2;
                break;
        }
        mActivity.runOnUiThread(() -> {
            PinpadDialog pinpadDialog = new PinpadDialog(mActivity, mCardNo, 20, 99, 2, 60, new OnPinpadDialogListener() {
                @Override
                public void onError(int errCode, String msg) {
                    LogUtil.se(getClass(), "input online pin onError:" + errCode);
                        mOnEmvListerCallback.onError(mActivity.getString(R.string.error_pinpad) + ":" + errCode + " " + msg);

                        MainApp.getInstance().getTransData().setHasPin(false);
                        mBundle.putInt(PosConstants.EMV.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, PosConstants.EMV.EmvPinConstraints.VERIFY_ERROR);
                        GcSmartPosUtils.getInstance().onSetPinResponse(MainApp.getInstance().getApplicationContext(), mBundle);
                }

                @Override
                public void onConfirm(byte[] data, boolean isNonePin) {
                    LogUtil.se(getClass(), "input online pin onConfirm isNonePin:" + isNonePin);
                    if (data != null) {
                        LogUtil.se(getClass(), "input online pin onConfirm data:" + new String(data));
                        MainApp.getInstance().getTransData().setPin(new String(data));
                        MainApp.getInstance().getTransData().setHasPin(true);
                    }
                        mBundle.putInt(PosConstants.EMV.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, PosConstants.EMV.EmvPinConstraints.VERIFY_SUCCESS);
                        mBundle.putByteArray(PosConstants.EMV.EmvPinConstraints.OUT_PIN_BLOCK, data);
                        GcSmartPosUtils.getInstance().onSetPinResponse(MainApp.getInstance().getApplicationContext(), mBundle);
                }

                @Override
                public void onCancel() {
                    LogUtil.se(getClass(), "input online pin onCancle");
                    try {
                        mOnEmvListerCallback.onError(mActivity.getString(R.string.error_cancel_trans));
                        bundle.putInt(PosConstants.EMV.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, PosConstants.EMV.EmvPinConstraints.VERIFY_CANCELED);
                        MainApp.getInstance().getTransData().setHasPin(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            pinpadDialog.show();
        });

    }

    @Override
    public void onRequestOnlineProcess(Bundle bundle) throws RemoteException {
        Log.e(TAG, "onRequestOnlineProcess");
        LogUtil.se(getClass(), "onRequestOnlineProcess: ");
        isAlreadyOnline = true;
        if (cardType == PosConstants.EMV.EmvSupportCardType.DEVICE_MAGSTRIPE) {
            LogUtil.se(getClass(), "onRequestOnlineProcess: DEVICE_MAGSTRIPE");
            mOnEmvListerCallback.onTransactionAuthRequest(null);
            GcSmartPosUtils.getInstance().stopTransaction(MainApp.getInstance().getApplicationContext());
        } else {
            byte[] field55 = bundle.getByteArray(PosConstants.EMV.EmvOnlineConstraints.EMV_DATA);
            LogUtil.se(getClass(), "onRequestOnlineProcess: " + BCDHelper.bcdToString(field55, 0, field55.length));
            try {
                TlvData tlvData = new TlvData(field55);

                String filed55 = GcSmartPosUtils.getInstance().EmvGetTlvDataList(MainApp.getInstance().getApplicationContext(), new String[]{
                        "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                        "82", "9F1A", "9F33", "9F34", "9F35", "9F1E", "84", "9F09", "9F41"
                });
                LogUtil.se(getClass(), "filed55:" + filed55);


//                String expData = tlvData.getEmvExpData();
//                LogUtil.se(getClass(), "expData:" + expData);

                String pan = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5A");
                LogUtil.se(getClass(), "pan:" + pan);

                String expData = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F24");
                LogUtil.se(getClass(), "expData:" + expData);

                String track2 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "57");

                LogUtil.se(getClass(), "track2:" + track2);

                String tag95 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "95");

                LogUtil.se(getClass(), "tag95:" + tag95);


                try {
                    PinpadResult temp = GcSmartPosUtils.getInstance().encryptMagTrack(MainApp.getInstance().getApplicationContext(), 20, 99, "5236497927826910D240120114350828");
                    LogUtil.se(getClass(), "temp:" + temp.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (track2 != null) {
//                    MainApp.getInstance().getTransData().setTrack2(track2);
//                    if (track2 != null) {
//                        String pan = DoCardInfoHelper.GetCardNumber(track2.getBytes(), track2.getBytes().length);
//                        LogUtil.se(getClass(), "pan:" + pan);
//                        MainApp.getInstance().getTransData().setPan(pan);
//                    }
//                }

                if (filed55 != null) {
                    MainApp.getInstance().getTransData().setSendIccData(filed55);
                }
                if (expData != null) {
                    MainApp.getInstance().getTransData().setExpDate(expData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            MainApp.getInstance().getTransData().setSendIccData(BCDHelper.bcdToString(field55, 0, field55.length));
            mOnEmvListerCallback.onTransactionAuthRequest(field55);
        }

    }

    @Override
    public void onTransactionResult(int result, Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onTransactionResult: " + result);
        if (isAlreadyOnline && MainApp.getInstance().isTestMode()) {
            result = PosConstants.EMV.PosEmvErrorCode.EMV_APPROVED;
        }

        Log.e(TAG, "isRequestSecondTap: " + bundle.getBoolean(PosConstants.EMV.EmvResultConstraints.IS_REQUEST_SECOND_TAP));
        int ret = 0;
        switch (result) {
            case PosConstants.EMV.PosEmvErrorCode.RUPAY_SECOND_TAP_DETECT_TIMEOUT:
                break;
            case PosConstants.EMV.PosEmvErrorCode.RUPAY_SECOND_TAP_NO_SAME_CARD:
                break;
            case PosConstants.EMV.PosEmvErrorCode.EMV_OK:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APPROVED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APPROVED_ONLINE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_FORCE_APPROVED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_DELAYED_APPROVED:
                LogUtil.se(getClass(), "EMV_APPROVED_ONLINE");
                ret = 0;
                break;
            case PosConstants.EMV.PosEmvErrorCode.EMV_DECLINED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_CANCEL:
            case PosConstants.EMV.PosEmvErrorCode.EMV_TIMEOUT:
            case PosConstants.EMV.PosEmvErrorCode.EMV_COMMAND_FAIL:
            case PosConstants.EMV.PosEmvErrorCode.EMV_FALLBACK:
            case PosConstants.EMV.PosEmvErrorCode.EMV_MULTI_CONTACTLESS:
            case PosConstants.EMV.PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APP_BLOCKED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_CARD_BLOCKED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APP_EMPTY:
            case PosConstants.EMV.PosEmvErrorCode.EMV_NOT_ALLOWED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_NOT_ACCEPTED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_TERMINATED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_SEE_PHONE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_OTHER_INTERFACE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_OTHER_ERROR:
            case PosConstants.EMV.PosEmvErrorCode.EMV_ENCRYPT_ERROR:
            case PosConstants.EMV.PosEmvErrorCode.EMV_UNENCRYPTED:
                LogUtil.se(getClass(), "EMV_UNENCRYPTED");
                ret = result;
                break;
            default:
                ret = -1;
                break;
        }
        // TODO
        // 测试演示时,都按照成功处理,
        int cvmCode = bundle.getInt(PosConstants.EMV.EmvResultConstraints.CVM);
        LogUtil.se(getClass(), "cvmCode:" + cvmCode);
        byte[] buff;
        buff = bundle.getByteArray(PosConstants.EMV.EmvResultConstraints.EMV_DATA);
        if (buff != null && buff.length > 0) {
            LogUtil.se(getClass(), "EMV_DATA:" + BCDHelper.bcdToString(buff, 0, buff.length));
        }
        buff = bundle.getByteArray(PosConstants.EMV.EmvResultConstraints.SCRIPT_RESULT);
        if (buff != null && buff.length > 0) {
            LogUtil.se(getClass(), "SCRIPT_RESULT:" + new String(buff));
        }

        GcSmartPosUtils.getInstance().stopTransaction(MainApp.getInstance().getApplicationContext());
        mOnEmvListerCallback.onDisplayTransReport(ret);
    }
}
