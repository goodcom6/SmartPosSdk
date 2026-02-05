package com.goodcom.pos.emv;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Arrays;

@Entity(tableName = "trans")//tableName设置表名
public class TransData implements Serializable, Cloneable {

    public static final String TRANS_PLUS = "+";
    public static final String TRANS_MINUS = "-";
    @Ignore
    public static final String REASON_NO_RECV = "98";
    @Ignore
    public static final String REASON_MACWRONG = "A0";
    @Ignore
    public static final String REASON_OTHERS = "06";
    private static final long serialVersionUID = 1L;
    //主键       自增
    @PrimaryKey()
    @NonNull
    private String uid;

    /* 脱机上送失败原因 */
    private String transState; // 交易状态 "+" 正交易 “-”负交易/已撤销
    private boolean isUpload; // 是否已批上送
    private boolean isOffUploadState; // 是否已脱机上送,true:脱机上送成功
    private int sendFailFlag; // 脱机上送失败类型 ：上送失败/平台拒绝
    private int sendTimes; // 已批上送次数
    private String transType; // 交易类型
    private String origTransType; // 原交易类型
    private String procCode; // 处理码，39域
    private String amount; // 交易金额
    private String tipAmount; // 小费金额
    private String balance; // 余额
    private String balanceFlag; // 余额标识C/D
    @NonNull
    private long transNo; // pos流水号
    private long origTransNo; // 原pos流水号
    private long batchNo; // 批次号
    private long origBatchNo; // 原批次号
    private String pan; // 主账号
    private String transferPan; // 转入卡卡号
    private String time; // 交易时间
    private String date; // 交易日期
    private String dateTime; // 交易日期时间
    private String origDate; // 原交易日期
    private String settleDate; // 清算日期
    private String expDate; // 卡有效期
    private int enterMode; // 输入模式
    private int transferEnterMode; // 转入卡的输入模式
    private String refNo; // 系统参考号
    private String origRefNo; // 原系统参考号
    private String authCode; // 授权码
    private String origAuthCode; // 原授权码
    private String isserCode; // 发卡行标识码
    private String acqCode; // 收单机构标识码
    private String acqCenterCode; // 受理方标识码,pos中心号(返回包时用)
    private String interOrgCode; // 国际组织代码
    private boolean hasPin; // 是否有输密码
    private String track1; // 磁道一信息
    private String track2; // 磁道二数据
    private String track3; // 磁道三数据
    private boolean isEncTrack; // 磁道是否加密
    private String reason; // 冲正原因
    private String reserved; // 63域附加域
    private String issuerResp; // 发卡方保留域
    private String centerResp; // 中国银联保留域
    private String recvBankResp;// 受理机构保留域
    private String scriptData; // 脚本数据
    private String phoneNo; // 持卡人手机号码
    private String authMode; // 授权方式
    private String authInsCode; // 授权机构代码
    private boolean isAdjustAfterUpload; // 离线结算上送后被调整，标识为true
    private boolean pinFree; // 免密
    private boolean signFree; // 免签
    private boolean isCDCVM; // CDCVM标识
    private boolean isOnlineTrans; // 是否为联机交易
    // =================EMV数据=============================
    // 电子签名专用
    private byte[] signData; // signData
    private int signSendState; // 上送状态：0，未上送；1，上送成功；2，上送失败
    private boolean signUpload; // 1:已重上送；0：未重上送
    private String receiptElements; // 电子签名时，55域签购单信息
    private boolean scriptExit;  //脚本 1  存在  0 不存在
    private boolean dupExit;//冲正 1  存在  0 不存在
    private String dupData; //冲正数据
    /**
     * EMV交易的执行状态
     */
    private byte emvResult; // EMV交易的执行状态
    private String cardSerialNo; // 23 域，卡片序列号
    private String sendIccData; // IC卡信息,55域
    private String dupIccData; // IC卡冲正信息,55域

    // =================EMV数据=============================
    private String tc; // IC卡交易证书(TC值)tag9f26,(BIN)
    private String arqc; // 授权请求密文(ARQC)
    private String arpc; // 授权响应密文(ARPC)
    private String tvr; // 终端验证结果(TVR)值tag95
    private String aid; // 应用标识符AID
    private String emvAppLabel; // 应用标签
    private String emvAppName; // 应用首选名称
    private String tsi; // 交易状态信息(TSI)tag9B
    private String atc; // 应用交易计数器(ATC)值tag9f36
    private String orderNo; // 订单号
    private String msgID;
    private String pin;
    private String srcInfo;
    private String oper;
    private String responseCode;
    private String responseMsg;
    private String termID;
    private String origTermID;
    private String merchID;
    private String header;
    private String tpdu;
    private boolean isReversal;
    private String field48;
    private String field60;
    private String field62;
    private boolean isSM; // 是否支持国密
    private String recvIccData;
    private String field3;

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getDupData() {
        return dupData;
    }

    public void setDupData(String dupData) {
        this.dupData = dupData;
    }

    public String getTransState() {
        return transState;
    }

    public void setTransState(String transState) {
        this.transState = transState;
    }

    public boolean getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public boolean getIsOffUploadState() {
        return isOffUploadState;
    }

    public void setIsOffUploadState(boolean isOffSend) {
        this.isOffUploadState = isOffSend;
    }

    public int getSendTimes() {
        return sendTimes;
    }

    public void setSendTimes(int sendTimes) {
        this.sendTimes = sendTimes;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getOrigTransType() {
        return origTransType;
    }

    public void setOrigTransType(String origTransType) {
        this.origTransType = origTransType;
    }

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getIssuerResp() {
        return issuerResp;
    }

    public void setIssuerResp(String issuerResp) {
        this.issuerResp = issuerResp;
    }

    public String getCenterResp() {
        return centerResp;
    }

    public void setCenterResp(String centerResp) {
        this.centerResp = centerResp;
    }

    public String getRecvBankResp() {
        return recvBankResp;
    }

    public void setRecvBankResp(String recvBankResp) {
        this.recvBankResp = recvBankResp;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalanceFlag() {
        return balanceFlag;
    }

    public void setBalanceFlag(String balanceFlag) {
        this.balanceFlag = balanceFlag;
    }

    public long getTransNo() {
        return transNo;
    }

    public void setTransNo(long transNo) {
        this.transNo = transNo;
    }

    public long getOrigTransNo() {
        return origTransNo;
    }

    public void setOrigTransNo(long origTransNo) {
        this.origTransNo = origTransNo;
    }

    public int getSendFailFlag() {
        return sendFailFlag;
    }

    public void setSendFailFlag(int sendFailFlag) {
        this.sendFailFlag = sendFailFlag;
    }

    public long getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(long batchNo) {
        this.batchNo = batchNo;
    }

    public long getOrigBatchNo() {
        return origBatchNo;
    }

    public void setOrigBatchNo(long origBatchNo) {
        this.origBatchNo = origBatchNo;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getTransferPan() {
        return transferPan;
    }

    public void setTransferPan(String transferPan) {
        this.transferPan = transferPan;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrigDate() {
        return origDate;
    }

    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public int getEnterMode() {
        return enterMode;
    }

    public void setEnterMode(int enterMode) {
        this.enterMode = enterMode;
    }

    public int getTransferEnterMode() {
        return transferEnterMode;
    }

    public void setTransferEnterMode(int transferEnterMode) {
        this.transferEnterMode = transferEnterMode;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getOrigRefNo() {
        return origRefNo;
    }

    public void setOrigRefNo(String origRefNo) {
        this.origRefNo = origRefNo;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOrigAuthCode() {
        return origAuthCode;
    }

    public void setOrigAuthCode(String origAuthCode) {
        this.origAuthCode = origAuthCode;
    }

    public String getIsserCode() {
        return isserCode;
    }

    public void setIsserCode(String isserCode) {
        this.isserCode = isserCode;
    }

    public String getAcqCode() {
        return acqCode;
    }

    public void setAcqCode(String acqCode) {
        this.acqCode = acqCode;
    }

    public String getAcqCenterCode() {
        return acqCenterCode;
    }

    public void setAcqCenterCode(String acqCenterCode) {
        this.acqCenterCode = acqCenterCode;
    }

    public String getInterOrgCode() {
        return interOrgCode;
    }

    public void setInterOrgCode(String interOrgCode) {
        this.interOrgCode = interOrgCode;
    }

    public boolean getHasPin() {
        return hasPin;
    }

    public void setHasPin(boolean hasPin) {
        this.hasPin = hasPin;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public boolean getIsEncTrack() {
        return isEncTrack;
    }

    public void setIsEncTrack(boolean isEncTrack) {
        this.isEncTrack = isEncTrack;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAuthInsCode() {
        return authInsCode;
    }

    public void setAuthInsCode(String authInsCode) {
        this.authInsCode = authInsCode;
    }

    public byte getEmvResult() {
        return emvResult;
    }

    public void setEmvResult(byte emvResult) {
        this.emvResult = emvResult;
    }

    public String getCardSerialNo() {
        return cardSerialNo;
    }

    public void setCardSerialNo(String cardSerialNo) {
        this.cardSerialNo = cardSerialNo;
    }

    public String getSendIccData() {
        return sendIccData;
    }

    public void setSendIccData(String sendIccData) {
        this.sendIccData = sendIccData;
    }

    public String getDupIccData() {
        return dupIccData;
    }

    public void setDupIccData(String dupIccData) {
        this.dupIccData = dupIccData;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getArqc() {
        return arqc;
    }

    public void setArqc(String arqc) {
        this.arqc = arqc;
    }

    public String getArpc() {
        return arpc;
    }

    public void setArpc(String arpc) {
        this.arpc = arpc;
    }

    public String getTvr() {
        return tvr;
    }

    public void setTvr(String tvr) {
        this.tvr = tvr;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getEmvAppLabel() {
        return emvAppLabel;
    }

    public void setEmvAppLabel(String emvAppLabel) {
        this.emvAppLabel = emvAppLabel;
    }

    public String getEmvAppName() {
        return emvAppName;
    }

    public void setEmvAppName(String emvAppName) {
        this.emvAppName = emvAppName;
    }

    public String getTsi() {
        return tsi;
    }

    public void setTsi(String tsi) {
        this.tsi = tsi;
    }

    public String getAtc() {
        return atc;
    }

    public void setAtc(String atc) {
        this.atc = atc;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSrcInfo() {
        return srcInfo;
    }

    public void setSrcInfo(String srcInfo) {
        this.srcInfo = srcInfo;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public String getMerchID() {
        return merchID;
    }

    public void setMerchID(String merchID) {
        this.merchID = merchID;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTpdu() {
        return tpdu;
    }

    public void setTpdu(String tpdu) {
        this.tpdu = tpdu;
    }

    public String getField48() {
        return field48;
    }

    public void setField48(String field48) {
        this.field48 = field48;
    }

    public String getField60() {
        return field60;
    }

    public void setField60(String field60) {
        this.field60 = field60;
    }

    public String getField62() {
        return field62;
    }

    public void setField62(String field62) {
        this.field62 = field62;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public boolean getPinFree() {
        return pinFree;
    }

    public void setPinFree(boolean pinFree) {
        this.pinFree = pinFree;
    }

    public boolean getSignFree() {
        return signFree;
    }

    public void setSignFree(boolean signFree) {
        this.signFree = signFree;
    }

    public boolean getIsCDCVM() {
        return isCDCVM;
    }

    public void setIsCDCVM(boolean isCDCVM) {
        this.isCDCVM = isCDCVM;
    }

    public byte[] getSignData() {
        return signData;
    }

    public void setSignData(byte[] signData) {
        this.signData = signData;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean getIsReversal() {
        return isReversal;
    }

    public void setIsReversal(boolean isReversal) {
        this.isReversal = isReversal;
    }

    public String getRecvIccData() {
        return recvIccData;
    }

    public void setRecvIccData(String recvIccData) {
        this.recvIccData = recvIccData;
    }

    public String getScriptData() {
        return scriptData;
    }

    public void setScriptData(String scriptData) {
        this.scriptData = scriptData;
    }

    public boolean getIsOnlineTrans() {
        return isOnlineTrans;
    }

    public void setIsOnlineTrans(boolean isOnlineTrans) {
        this.isOnlineTrans = isOnlineTrans;
    }

    public boolean getIsSM() {
        return isSM;
    }

    public void setIsSM(boolean isSM) {
        this.isSM = isSM;
    }

    public String getOrigTermID() {
        return origTermID;
    }

    public void setOrigTermID(String origTermID) {
        this.origTermID = origTermID;
    }

    public String getReceiptElements() {
        return receiptElements;
    }

    public void setReceiptElements(String receiptElements) {
        this.receiptElements = receiptElements;
    }

    public boolean isScriptExit() {
        return scriptExit;
    }

    public void setScriptExit(boolean scriptExit) {
        this.scriptExit = scriptExit;
    }

    public boolean isDupExit() {
        return dupExit;
    }

    public void setDupExit(boolean dupExit) {
        this.dupExit = dupExit;
    }

    public int getSignSendState() {
        return signSendState;
    }

    public void setSignSendState(int signSendState) {
        this.signSendState = signSendState;
    }

    public boolean getSignUpload() {
        return signUpload;
    }

    public void setSignUpload(boolean signUpload) {
        this.signUpload = signUpload;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public boolean getIsAdjustAfterUpload() {
        return isAdjustAfterUpload;
    }

    public void setIsAdjustAfterUpload(boolean isAdjustAfterUpload) {
        this.isAdjustAfterUpload = isAdjustAfterUpload;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "TransData{" +
                "uid=" + uid +
                ", transState='" + transState + '\'' +
                ", isUpload=" + isUpload +
                ", isOffUploadState=" + isOffUploadState +
                ", sendFailFlag=" + sendFailFlag +
                ", sendTimes=" + sendTimes +
                ", transType='" + transType + '\'' +
                ", origTransType='" + origTransType + '\'' +
                ", procCode='" + procCode + '\'' +
                ", amount='" + amount + '\'' +
                ", tipAmount='" + tipAmount + '\'' +
                ", balance='" + balance + '\'' +
                ", balanceFlag='" + balanceFlag + '\'' +
                ", transNo=" + transNo +
                ", origTransNo=" + origTransNo +
                ", batchNo=" + batchNo +
                ", origBatchNo=" + origBatchNo +
                ", pan='" + pan + '\'' +
                ", transferPan='" + transferPan + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", origDate='" + origDate + '\'' +
                ", settleDate='" + settleDate + '\'' +
                ", expDate='" + expDate + '\'' +
                ", enterMode=" + enterMode +
                ", transferEnterMode=" + transferEnterMode +
                ", refNo='" + refNo + '\'' +
                ", origRefNo='" + origRefNo + '\'' +
                ", authCode='" + authCode + '\'' +
                ", origAuthCode='" + origAuthCode + '\'' +
                ", isserCode='" + isserCode + '\'' +
                ", acqCode='" + acqCode + '\'' +
                ", acqCenterCode='" + acqCenterCode + '\'' +
                ", interOrgCode='" + interOrgCode + '\'' +
                ", hasPin=" + hasPin +
                ", track1='" + track1 + '\'' +
                ", track2='" + track2 + '\'' +
                ", track3='" + track3 + '\'' +
                ", isEncTrack=" + isEncTrack +
                ", reason='" + reason + '\'' +
                ", reserved='" + reserved + '\'' +
                ", issuerResp='" + issuerResp + '\'' +
                ", centerResp='" + centerResp + '\'' +
                ", recvBankResp='" + recvBankResp + '\'' +
                ", scriptData='" + scriptData + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", authMode='" + authMode + '\'' +
                ", authInsCode='" + authInsCode + '\'' +
                ", isAdjustAfterUpload=" + isAdjustAfterUpload +
                ", pinFree=" + pinFree +
                ", signFree=" + signFree +
                ", isCDCVM=" + isCDCVM +
                ", isOnlineTrans=" + isOnlineTrans +
                ", signData=" + Arrays.toString(signData) +
                ", signSendState=" + signSendState +
                ", signUpload=" + signUpload +
                ", receiptElements='" + receiptElements + '\'' +
                ", scriptExit=" + scriptExit +
                ", dupExit=" + dupExit +
                ", dupData='" + dupData + '\'' +
                ", emvResult=" + emvResult +
                ", cardSerialNo='" + cardSerialNo + '\'' +
                ", sendIccData='" + sendIccData + '\'' +
                ", dupIccData='" + dupIccData + '\'' +
                ", tc='" + tc + '\'' +
                ", arqc='" + arqc + '\'' +
                ", arpc='" + arpc + '\'' +
                ", tvr='" + tvr + '\'' +
                ", aid='" + aid + '\'' +
                ", emvAppLabel='" + emvAppLabel + '\'' +
                ", emvAppName='" + emvAppName + '\'' +
                ", tsi='" + tsi + '\'' +
                ", atc='" + atc + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", msgID='" + msgID + '\'' +
                ", pin='" + pin + '\'' +
                ", srcInfo='" + srcInfo + '\'' +
                ", oper='" + oper + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseMsg='" + responseMsg + '\'' +
                ", termID='" + termID + '\'' +
                ", origTermID='" + origTermID + '\'' +
                ", merchID='" + merchID + '\'' +
                ", header='" + header + '\'' +
                ", tpdu='" + tpdu + '\'' +
                ", isReversal=" + isReversal +
                ", field48='" + field48 + '\'' +
                ", field60='" + field60 + '\'' +
                ", field62='" + field62 + '\'' +
                ", isSM=" + isSM +
                ", recvIccData='" + recvIccData + '\'' +
                ", field3='" + field3 + '\'' +
                '}';
    }

    public enum ETransStatus {
        NORMAL,
        VOID,
        ADJUST
    }

    public static class OfflineStatus {
        /**
         * 脱机上送失败
         */
        public static final int SEND_ERR_SEND = 0x01;
        /**
         * 脱机上送平台拒绝(返回码非00)
         */
        public static final int SEND_ERR_RESP = 0x02;
        /**
         * 脱机上送未知失败原因
         */
        public static final int SEND_ERR_UNKN = 0xff;
    }

    /**
     * 电子签名上送状态
     */
    public static class SignSendStatus {
        public static final int SEND_SIG_NO = 0x00; // 未上送
        public static final int SEND_SIG_SUCC = 0x01; // 上送成功
        public static final int SEND_SIG_ERR = 0X02; // 上送失败
    }

    public static class EnterMode {
        /**
         * 手工输入
         */
        public static final int MANAUL = 1;
        /**
         * 刷卡
         */
        public static final int SWIPE = 2;
        /**
         * 插卡
         */
        public static final int INSERT = 3;
        /**
         * IC卡回退
         */
        public static final int FALLBACK = 4;
        /**
         * 预约支付
         */
        public static final int PHONE = 5;
        /**
         * 非接快速支付
         */
        public static final int PICC = 6;
        /**
         * 非接完整PBOC
         */
        public static final int CLSS_PBOC = 7;
        /**
         * 非接读取CUPMobile
         */
        public static final int MOBILE = 8;
        /**
         * 扫码支付
         */
        public static final int QR = 9;
    }
}
