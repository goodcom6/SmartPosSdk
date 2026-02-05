package com.goodcom.pos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.goodcom.pos.dialog.EnterDialog;
import com.goodcom.pos.dialog.LoadingDialog;
import com.goodcom.smartpossdk.GcSmartPosUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import com.goodcom.smartpossdk.utils.LogUtil;

public abstract class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "BaseAppCompatActivity";

    private LoadingDialog loadDialog;

    private final Handler dlgHandler = new Handler();
    private final Map<String, Long> timeMap = new LinkedHashMap<>();
    private EnterDialog mEnterDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setStatusBarColor();
//        MyApplication.initLocaleLanguage();
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void initToolbarBringBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Navigation Icon 要设定在 setSupportActionBar 才有作用 否则会出現 back button
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> {
                    GcSmartPosUtils.getInstance().stopTransaction(MainApp.getInstance().getApplicationContext());
                    finish();
                }
        );
    }

    public void initToolbarBringBack(int resId) {
        initToolbarBringBack(getString(resId));
    }

    public void initToolbarBringBack(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );
    }

    public void showToast(int redId) {
        showToastOnUI(getString(redId));
    }


    public void showToast(String msg) {
        showToastOnUI(msg);
    }

    private void showToastOnUI(final String msg) {
        runOnUiThread(
                () -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }


    protected void showLoadingDialog(int resId) {
        runOnUiThread(() -> _showLoadingDialog(getString(resId)));
    }

    protected void showLoadingDialog(final String msg) {
        runOnUiThread(() -> _showLoadingDialog(msg));
    }

    /**
     * This method should be called in UI thread
     */
    private void _showLoadingDialog(final String msg) {
        if (loadDialog == null) {
            loadDialog = new LoadingDialog(this, msg);
        } else {
            loadDialog.setMessage(msg);
        }
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
    }

    protected void dismissLoadingDialog() {
        runOnUiThread(
                () -> {
                    if (loadDialog != null && loadDialog.isShowing()) {
                        loadDialog.dismiss();
                    }
                    dlgHandler.removeCallbacksAndMessages(null);
                }
        );
    }


    protected void openActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(this, clazz);
        openActivity(intent, false);
    }

    protected void openActivity(Class<? extends Activity> clazz, boolean finishSelf) {
        Intent intent = new Intent(this, clazz);
        openActivity(intent, finishSelf);
    }

    protected void openActivity(Intent intent, boolean finishSelf) {
        startActivity(intent);
        if (finishSelf) {
            finish();
        }
    }

    protected void openActivityForResult(Class<? extends Activity> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        openActivityForResult(intent, requestCode);
    }

    protected void openActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View v) {

    }

    protected void addStartTime(String key) {
        timeMap.put("start_" + key, SystemClock.elapsedRealtime());
    }

    protected void addStartTimeWithClear(String key) {
        timeMap.clear();
        timeMap.put("start_" + key, SystemClock.elapsedRealtime());
    }

    protected void addEndTime(String key) {
        timeMap.put("end_" + key, SystemClock.elapsedRealtime());
    }

    protected void showSpendTime() {
        Long startValue = null, endValue = null;
        for (String key : timeMap.keySet()) {
            if (!key.startsWith("start_")) {
                continue;
            }
            key = key.substring("start_".length());
            startValue = timeMap.get("start_" + key);
            endValue = timeMap.get("end_" + key);
            if (startValue == null || endValue == null) {
                continue;
            }
            LogUtil.e(getClass(), key + ", spend time(ms):" + (endValue - startValue));
        }
    }


    protected boolean showConfirmDialog(final String title, final String message) {
        final BlockingQueue<Boolean> queue = new SynchronousQueue<Boolean>();
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                mEnterDialog = new EnterDialog(BaseAppCompatActivity.this);

                mEnterDialog.showConfirmDialog(title, message,
                        new EnterDialog.OnConfirmListener() {

                            @Override
                            public void onConfirm() {
                                try {
                                    queue.put(true);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancel() {
                                try {
                                    queue.put(false);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        });

            }
        });
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected int showListChoseDialog(final String title, final String[] list) {
        final BlockingQueue<Integer> queue = new SynchronousQueue<Integer>();
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                mEnterDialog = new EnterDialog(BaseAppCompatActivity.this);
                mEnterDialog.showListChoseDialog(title, list,
                        new EnterDialog.OnChoseListener() {

                            @Override
                            public void Chose(int i) {
                                try {
                                    queue.put(i);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

            }
        });
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
