package com.goodcom.pos.emv;

import android.os.RemoteException;
import android.util.Log;

import com.goodcom.pos.MainApp;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.entity.PosEmvAid;
import com.goodcom.smartpossdk.utils.PosUtils;


public class EmvConfigNew1 {
    private static final String TAG = "EmvConfig";


    public static void loadAid() throws RemoteException {
        PosEmvAid aid;


        // VISA
        aid = addAid("A000000003", "008C");
        aid.ContactlessTransLimit = 200001;
        aid.ContactlessCVMLimit = 1;
        aid.SelectIndicator = true;
        aid.qualifiers =  PosUtils.hexStringToBytes("36000000");
        aid.TerminalType = "22".getBytes();
        aid.TerminalCapabilities = "E0F8C8".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F001".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // Unionpay
        aid = addAid("A000000333", "0020");
        aid.SelectIndicator = true;
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("d84004f800");
        aid.TACDefault = PosUtils.hexStringToBytes("084000a800");

        aid.qualifiers =  PosUtils.hexStringToBytes("36000000");
        aid.isQualifiers = true;
        aid.TerminalType = "22".getBytes();
        aid.TerminalCapabilities = "E0F8C8".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F001".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);



        // MasterCard
        aid = addAid("A000000004", "0002");
        aid.SelectIndicator = true;
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0400000000");
        aid.TACOnline = PosUtils.hexStringToBytes("f850acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc50aca000");


        aid.qualifiers =  PosUtils.hexStringToBytes("DF81180160DF81190108DF811E0160DF812C0108");

        aid.TerminalType = "22".getBytes();
        aid.TerminalCapabilities = "E0F8C8".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F001".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        aid.TerminalRiskManagementData = PosUtils.hexStringToBytes("6C00000000000000");
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // Discover
        aid = addAid("A000000152", "0001");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fce09cf800");
        aid.TACDefault = PosUtils.hexStringToBytes("dc00002000");
        aid.qualifiers =  PosUtils.hexStringToBytes("B600C000");
        aid.TerminalType = "22".getBytes();
        aid.TerminalCapabilities = "E0F8C8".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F001".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);

        // AMEX
        aid = addAid("A000000025", "0001");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0000000000");
        aid.TACOnline = PosUtils.hexStringToBytes("c800000000");
        aid.TACDefault = PosUtils.hexStringToBytes("c800000000");
        aid.qualifiers = PosUtils.hexStringToBytes("DCE00004");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0F8C8".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F001".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // JCB
        aid = addAid("A000000065", "0021");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aid.qualifiers = PosUtils.hexStringToBytes("F70000FF");
        aid.TerminalType = "22".getBytes();
        aid.TerminalCapabilities = "E0F8C8".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F001".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);

    }

    public static void loadAid1() throws RemoteException {
        PosEmvAid aid;

        // VISA
        aid = addAid("A000000003", "008C");
        aid.ContactlessTransLimit = 200001;
        aid.ContactlessCVMLimit = 1;
        aid.SelectIndicator = true;
        aid.qualifiers =  PosUtils.hexStringToBytes("00000000");

        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // Unionpay
        aid = addAid("A000000333", "0020");
        aid.SelectIndicator = true;
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("d84004f800");
        aid.TACDefault = PosUtils.hexStringToBytes("097800a800");
        aid.qualifiers =  PosUtils.hexStringToBytes("00000000");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);



        // MasterCard
        aid = addAid("A000000004", "0002");
        aid.SelectIndicator = true;
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0400000000");
        aid.TACOnline = PosUtils.hexStringToBytes("f850acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc50aca000");
        aid.qualifiers =  PosUtils.hexStringToBytes("DF81180100DF81190100DF811E0100DF812C0100");

        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        aid.TerminalRiskManagementData = PosUtils.hexStringToBytes("0000000000000000");

        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // Discover
        aid = addAid("A000000152", "0001");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fce09cf800");
        aid.TACDefault = PosUtils.hexStringToBytes("dc00002000");
        aid.qualifiers =  PosUtils.hexStringToBytes("00000000");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);

        // AMEX
        aid = addAid("A000000025", "0001");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0000000000");
        aid.TACOnline = PosUtils.hexStringToBytes("c800000000");
        aid.TACDefault = PosUtils.hexStringToBytes("c800000000");
        aid.qualifiers = PosUtils.hexStringToBytes("00000000");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // JCB
        aid = addAid("A000000065", "0021");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aid.qualifiers = PosUtils.hexStringToBytes("00000000");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);

    }

    public static void loadAid2() throws RemoteException {
        PosEmvAid aid;

        // VISA
        aid = addAid("A000000003", "008C");
        aid.ContactlessTransLimit = 200001;
        aid.ContactlessCVMLimit = 1;
        aid.SelectIndicator = true;
        aid.qualifiers =  PosUtils.hexStringToBytes("FFFFFFFF");

        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // Unionpay
        aid = addAid("A000000333", "0020");
        aid.SelectIndicator = true;
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("d84004f800");
        aid.TACDefault = PosUtils.hexStringToBytes("097800a800");
        aid.qualifiers =  PosUtils.hexStringToBytes("FFFFFFFF");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);



        // MasterCard
        aid = addAid("A000000004", "0002");
        aid.SelectIndicator = true;
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0400000000");
        aid.TACOnline = PosUtils.hexStringToBytes("f850acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc50aca000");
        aid.qualifiers = PosUtils.hexStringToBytes("DF811801FFDF811901FFDF811E01FFDF812C01FF");

        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        aid.TerminalRiskManagementData = PosUtils.hexStringToBytes("FFFFFFFFFFFFFFFF");
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // Discover
        aid = addAid("A000000152", "0001");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fce09cf800");
        aid.TACDefault = PosUtils.hexStringToBytes("dc00002000");
        aid.qualifiers =  PosUtils.hexStringToBytes("FFFFFFFF");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);

        // AMEX
        aid = addAid("A000000025", "0001");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0000000000");
        aid.TACOnline = PosUtils.hexStringToBytes("c800000000");
        aid.TACDefault = PosUtils.hexStringToBytes("c800000000");
        aid.qualifiers = PosUtils.hexStringToBytes("FFFFFFFF");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);


        // JCB
        aid = addAid("A000000065", "0021");
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aid.qualifiers = PosUtils.hexStringToBytes("FFFFFFFF");
        aid.TerminalType = "21".getBytes();
        aid.TerminalCapabilities = "E0FFFF".getBytes();
        aid.AdditionalTerminalCapabilities = "F000F0F00F".getBytes();
        aid.TerminalCountryCode = "0978".getBytes();
        aid.MerchantCategoryCode = "0978".getBytes();
        aid.TransCurrencyCode = "0978".getBytes();
        aid.TransCurrencyExp = "02".getBytes();
        GcSmartPosUtils.getInstance().EmvSetAid(MainApp.getInstance().getApplicationContext(), aid);

    }


    private static PosEmvAid addAid(String aid, String version) {
        PosEmvAid appList = new PosEmvAid();
        appList.AID = PosUtils.hexStringToBytes(aid);
        appList.Version = PosUtils.hexStringToBytes(version);
        appList.SelectIndicator = true;
        appList.dDOL = PosUtils.hexStringToBytes("9F0206");
        appList.tDOL = PosUtils.hexStringToBytes("9F3704");
        appList.TACDenial = PosUtils.hexStringToBytes("0010000000");
        appList.TACOnline = PosUtils.hexStringToBytes("dc4004f800");
        appList.TACDefault = PosUtils.hexStringToBytes("dc4000a800");
        appList.Threshold = 10000;
        appList.TargetPercentage = 0;
        appList.MaxTargetPercentage = 99;
        appList.FloorLimit = 0;
        appList.ContactlessTransLimit = 999999999;
        appList.ContactlessCVMLimit = 100;
        appList.ContactlessFloorLimit = 0;
        appList.DynamicTransLimit = 999999999;

        appList.TerminalType = "22".getBytes();
        appList.TerminalCapabilities = "E0F8C8".getBytes();
        appList.AdditionalTerminalCapabilities = "F000F0F001".getBytes();
        appList.TerminalCountryCode = "0978".getBytes();
        appList.MerchantCategoryCode = "0978".getBytes();
        appList.TransCurrencyCode = "0978".getBytes();
        appList.TransCurrencyExp = "02".getBytes();
        appList.isQualifiers = true;
        appList.TypeIndicator = true;
        Log.e(TAG,"TypeIndicator:"+appList.TypeIndicator);
        return appList;
    }



}
