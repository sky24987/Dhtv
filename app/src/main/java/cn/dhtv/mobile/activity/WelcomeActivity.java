package cn.dhtv.mobile.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.model.CategoryInitiator;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.provider.MyContentProvider;
import cn.dhtv.mobile.service.DailyService;

public class WelcomeActivity extends Activity {


    private FrameLayout mLayout;
    CategoryInitiator mCategoryInitiator;
    private CategoryInitiator.CallBacks mInitCallBacks;
    private SharedPreferences mAppPreferences;
    private InitiateStateDialog mInitiateStateDialog;
    private InitTimeOutRunnable mInitTimeOutRunnable;
    private Handler mHandler;



    private boolean animationComplete = false;
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout = (FrameLayout)getLayoutInflater().inflate(R.layout.activity_welcome,null);
        setContentView(mLayout);
        init();

        Intent dailyServiceIntent = new Intent(this, DailyService.class);
        startService(dailyServiceIntent);
        mAccount = createSyncAccount(this);
        /*test();*/
        /*mAppPreferences.edit().putBoolean(Data.PREFERENCE_KEY_APP_INITIATED,true).commit();*/
        if(mAppPreferences.getBoolean(Data.PREFERENCE_KEY_APP_INITIATED,false) == false){
            initApp();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(2000);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationComplete = true;checkContinue();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLayout.setAnimation(aa);
    }

    private void initApp(){
        if(mAppPreferences.getBoolean(Data.PREFERENCE_KEY_APP_INITIATED,false) == false){
            mInitCallBacks =new CategoryInitiator.CallBacks() {
                @Override
                public void onSuccess(int flag) {

                    onInitiated();
                }

                @Override
                public void onFail(int flag) {
                    onInitiatedFail();
                }
            };
            mCategoryInitiator = new CategoryInitiator();
            mCategoryInitiator.setCallBacks(mInitCallBacks);
            mCategoryInitiator.asyncInit();

            mHandler.postDelayed(mInitTimeOutRunnable, 5000);
        }

    }

    private void onInitiated(){
        mHandler.removeCallbacks(mInitTimeOutRunnable);
        mAppPreferences.edit().putBoolean(Data.PREFERENCE_KEY_APP_INITIATED,true).commit();
        checkContinue();
    }



    private void onInitiatedFail(){
        mHandler.removeCallbacks(mInitTimeOutRunnable);
        if(mInitiateStateDialog == null){
            mInitiateStateDialog = new InitiateStateDialog(this);
        }
        mInitiateStateDialog.show();
    }

    private void checkContinue(){
        if(animationComplete && mAppPreferences.getBoolean(Data.PREFERENCE_KEY_APP_INITIATED,false)){
            toNextActivity();
        }
    }

    private void init(){
        mAppPreferences = getSharedPreferences(Data.PREFERENCE_NAME_APP,MODE_PRIVATE);
        mInitTimeOutRunnable = new InitTimeOutRunnable();
        mHandler = new Handler();
    }

    private void toNextActivity(){
        Intent intent = new Intent(WelcomeActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                Data.DUMMY_ACCOUNT, Data.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            ContentResolver.setIsSyncable(newAccount, MyContentProvider.AUTHORITY,1);
            ContentResolver.setSyncAutomatically(newAccount, MyContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(newAccount, MyContentProvider.AUTHORITY,
                    new Bundle(), Data.SYNC_PERIOD_ONE_DAY);
            return newAccount;
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            return newAccount;
        }
    }

    private void test(){
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, MyContentProvider.AUTHORITY,settingsBundle);

    }


    private class InitTimeOutRunnable implements Runnable{
        @Override
        public void run() {
            if(mAppPreferences.getBoolean(Data.PREFERENCE_KEY_APP_INITIATED,false) == true){
                //TODO
//                        onInitiated();
            }else {
                mCategoryInitiator.cancelAllTask();
                mCategoryInitiator.setCallBacks(null);
                onInitiatedFail();
                //TODO
            }
        }
    }

    private class InitiateStateDialog extends AlertDialog {
        private OnClickListener mNegativeListener;
        private OnClickListener mPositiveListener;

        private InitiateStateDialog(Context context) {
            super(context);
            init();
            setCanceledOnTouchOutside(false);
            setFinishOnTouchOutside(false);
            setTitle(R.string.init_dialog_hint);
            setButton(BUTTON_NEGATIVE, getContext().getString(R.string.init_dialog_negative), mNegativeListener);
            setButton(BUTTON_POSITIVE,getContext().getString(R.string.init_dialog_positive),mPositiveListener);
        }

        private void init(){
            mNegativeListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WelcomeActivity.this.finish();
                }
            };

            mPositiveListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    initApp();
                }
            };
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_BACK){
                WelcomeActivity.this.finish();
                return true;
            }
            return false;
        }
    }
}
