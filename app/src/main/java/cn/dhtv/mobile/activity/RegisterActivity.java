package cn.dhtv.mobile.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;
import cn.dhtv.mobile.util.TextUtils;
import cn.dhtv.mobile.util.TimeUtils;

public class RegisterActivity extends ActionBarActivity {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private static final int SMS_DISABLE_TIME_SECOND = 60;
    private static final String PREFERENCE_KEY_SMS_RESEND_TIME = "SMS_RESEND_TIME";

    private EditText userNameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText repeatPasswordView;
    private EditText mobileView;
    private EditText smsCodeView;
    private Button submitButton, getSMSCodeButton;

    private Dialog processDialog;

    private RegisterAsyncTask mRegisterAsyncTask;

    private int smsRestSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userNameView = (EditText) findViewById(R.id.user_name);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        repeatPasswordView = (EditText) findViewById(R.id.repeat_password);
        mobileView = (EditText) findViewById(R.id.mobile);
        smsCodeView = (EditText) findViewById(R.id.sms_code);
        submitButton = (Button) findViewById(R.id.register);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameView.getText().toString();
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                String repeatPassword = repeatPasswordView.getText().toString();
                String mobile = mobileView.getText().toString();
                String smsCode = smsCodeView.getText().toString();

                if(!TextUtils.validUserName(userName)){
                    showMessage(getString(R.string.message_wrong_user_name));
                    userNameView.requestFocus();
                    return;
                }
                if(!TextUtils.validEmail(email)){
                    showMessage(getString(R.string.message_wrong_email));
                    emailView.requestFocus();
                    return;
                }
                if(!TextUtils.validPassword(password)){
                    showMessage(getString(R.string.message_wrong_password));
                    passwordView.requestFocus();
                    return;
                }
                if(!password.equals(repeatPassword)){
                    showMessage(getString(R.string.message_wrong_passwrod_different));
                    repeatPasswordView.requestFocus();
                    return;
                }
                if(!TextUtils.validPhoneNumber(mobile)){
                    showMessage(getString(R.string.message_wrong_phone_number));
                    mobileView.requestFocus();
                    return;
                }
                if(!TextUtils.validSMSCode(smsCode)){
                    showMessage(getString(R.string.message_wrong_sms_code));
                    smsCodeView.requestFocus();
                    return;
                }

                register(userName, email, password, repeatPassword, mobile, smsCode);
            }
        });
        getSMSCodeButton = (Button) findViewById(R.id.button_get_sms_code);
        getSMSCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mobileView.getText().toString();
                if(StringUtils.isNumeric(phoneNumber) &&phoneNumber.length() == 11){
                    requestSms(phoneNumber);
                }else {
                    Toast.makeText(RegisterActivity.this,R.string.message_wrong_phone_number,Toast.LENGTH_SHORT);
                }
            }

            private void requestSms(String phone){
                new RequestSmsCodeAsyncTask(phone).executeOnExecutor(Singletons.getAsyncTaskThreadPoolExecutor());
            }
        });
        checkSMSButtonState();
        processDialog = new ProcessingDialog(this, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mRegisterAsyncTask.cancelNetwork();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeSmsResendTargetTime();
        cancelTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void register(String username,String email,String pw,String pwt,String mobile,String smsCode){
        //TODO
        processDialog.show();
        mRegisterAsyncTask = new RegisterAsyncTask(username, email, pw, pwt, mobile, smsCode);
        mRegisterAsyncTask.executeOnExecutor(Singletons.getAsyncTaskThreadPoolExecutor());

    }

    private void cancelTasks(){
        //TODO
    }

    private void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void storeSmsResendTargetTime(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        sharedPreferences.edit().putLong(PREFERENCE_KEY_SMS_RESEND_TIME,System.currentTimeMillis()+smsRestSeconds* 1000).commit();
    }

    private Long getSmsResendTargetTime(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getLong(PREFERENCE_KEY_SMS_RESEND_TIME,0);
    }

    private void checkSMSButtonState(){

        long time = getSmsResendTargetTime();
        Date target = (time == 0 ? null : new Date(time));
        int restSeconds = TimeUtils.restTimeSeconds(target);
        if(restSeconds == 0){
            return;
        }

        new RefreshSendSMSButtonAsyncTask().executeOnExecutor(Singletons.getAsyncTaskThreadPoolExecutor(),new Integer(restSeconds));
    }

    private class RequestSmsCodeAsyncTask extends AsyncTask<Object,Object,UserCenter.SMSCodeRequestMessage>{
        String phone;

        public RequestSmsCodeAsyncTask(String phone) {
            this.phone = phone;
        }

        @Override
        protected UserCenter.SMSCodeRequestMessage doInBackground(Object... params) {
            UserCenter.SMSCodeRequestMessage smsCodeRequestMessage = null;
            try{
                smsCodeRequestMessage = UserCenter.getInstance().requestSMSCode(phone);
            }catch (IOException e){

            }catch (JSONException e){

            }

            return smsCodeRequestMessage;
        }

        @Override
        protected void onPostExecute(UserCenter.SMSCodeRequestMessage smsCodeRequestMessage) {
            if(smsCodeRequestMessage == null){
                Toast.makeText(RegisterActivity.this,"短信获取失败",Toast.LENGTH_SHORT);
                return;
            }

            if(smsCodeRequestMessage.isSuccess()){
                new RefreshSendSMSButtonAsyncTask().executeOnExecutor(Singletons.getAsyncTaskThreadPoolExecutor(),new Integer(SMS_DISABLE_TIME_SECOND));
            }else {
                Toast.makeText(RegisterActivity.this,smsCodeRequestMessage.getMessage(),Toast.LENGTH_SHORT);
                return;
            }
        }
    }

    private class RefreshSendSMSButtonAsyncTask extends AsyncTask<Integer,Integer,Integer>{
        @Override
        protected void onPreExecute() {
            getSMSCodeButton.setEnabled(false);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            if(params.length == 0){
                return null;
            }
            int seconds = params[0].intValue();
            for(;seconds >= 0;seconds --){
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    return new Integer(seconds);
                }
                publishProgress(new Integer(seconds));
            }
            return new Integer(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(values.length == 0){
                return;
            }

            smsRestSeconds = values[0].intValue();
            showSeconds(smsRestSeconds);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int seconds = integer.intValue();
            if(seconds == 0){
                getSMSCodeButton.setText(R.string.button_get_sms_code);
                getSMSCodeButton.setEnabled(true);
            }else {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                sharedPreferences.edit().putLong(PREFERENCE_KEY_SMS_RESEND_TIME,System.currentTimeMillis()+seconds* 1000).commit();
            }
        }

        private void showSeconds(int seconds){
            //TODO
            String hint = seconds + getString(R.string.hint_sms_resend);
            getSMSCodeButton.setText(hint);
        }
    }

    private class RegisterAsyncTask extends AsyncTask<Object,Object,UserCenter.RegisterMessage>{
        String userName;
        String email;
        String password;
        String repeatPassword;
        String mobile;
        String smsCode;
        boolean ioException = false;

        public RegisterAsyncTask(String userName, String email, String password, String repeatPassword, String mobile, String smsCode) {
            this.userName = userName;
            this.email = email;
            this.password = password;
            this.repeatPassword = repeatPassword;
            this.mobile = mobile;
            this.smsCode = smsCode;
        }

        @Override
        protected UserCenter.RegisterMessage doInBackground(Object[] params) {
            UserCenter.RegisterMessage registerMessage = null;
            try {
                registerMessage = UserCenter.getInstance().register(userName, email, password, repeatPassword, mobile, smsCode);
            }catch (IOException e){
                ioException = true;
                if(DEBUG){
                    Log.d(LOG_TAG,"注册网络异常");
                }
            }catch (JSONException e){

            }
            return registerMessage;
        }

        @Override
        protected void onPostExecute(UserCenter.RegisterMessage registerMessage) {
            processDialog.hide();
            if(registerMessage == null){
                showMessage("注册失败");
                return;
            }

            if(registerMessage.isSuccess()){
                showMessage(registerMessage.getMessage());
                User user = registerMessage.getUser();
                UserCenter.getInstance().storeUser(RegisterActivity.this,user);

                //TODO wait a minute
                setResult(RESULT_OK);
                finish();
            }else {
                showMessage(registerMessage.getMessage());
            }
        }

        @Override
        protected void onCancelled(UserCenter.RegisterMessage registerMessage) {
            processDialog.hide();
            if(ioException) {
                showMessage("取消注册");
            }

        }

       public void cancelNetwork(){
           cancel(true);
           UserCenter.getInstance().cancelRegister();

       }


    }

}
