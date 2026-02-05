
This app demonstrates how to use Goodcom's financial POS device SDK, which supports IC cards, NFC cards, magnetic stripe cards, and pin pads.
<p float="left">
  <img src="images/demoUi.png" width="30%" />

</p>

# Getting Started

Import the SDK library in the app's build.gradle file: 
```javascript
implementation("cn.goodcom:smartpossdk:1.+")
```

## Import in code
Declare GcSmartPosUtils classes where the SDK is used
```javascript
import com.goodcom.smartpossdk.GcSmartPosUtils;
```

## Initialize SDK
You need to initialize before using the SDK's API methods.
```javascript
GcSmartPosUtils.getInstance().init(this, new ConnectCallback() {
            @Override
            public void onConnectPaySDK() {
                //test(); //Call the API for operating on pos
            }
        });
```

## Use api
Use GcSmartPosUtils classes to operate the POS system using relevant APIs, such as reading IC cards, NFC cards, and magnetic stripe cards.