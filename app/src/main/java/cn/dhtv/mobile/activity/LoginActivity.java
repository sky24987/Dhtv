package cn.dhtv.mobile.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;

public class LoginActivity extends ActionBarActivity {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private static final int REQUEST_CODE_REGISTER = 1;

    private EditText userNameView;
    private EditText passwordView;
    private View login;
    private View register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameView = (EditText) findViewById(R.id.user_name);
        passwordView = (EditText) findViewById(R.id.password);
        login = findViewById(R.id.button_login_in);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userNameView.getText().toString();
                String password = passwordView.getText().toString();
                if(DEBUG){
                    Log.d(LOG_TAG,name);
                    Log.d(LOG_TAG, password);
                }

                new LoginAsyncTask(name,password).executeOnExecutor(Singletons.getAsyncTaskThreadPoolExecutor());

            }
        });
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegister();
            }
        });

       User user =  UserCenter.getInstance().retrieveUser(this);
        if(user != null){
            userNameView.setText(user.getName());
        }
    }

    private void toRegister(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivityForResult(intent,REQUEST_CODE_REGISTER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_login, menu);*/
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

    private void showLoginSuccess(){
        //TODO;
    }

    private class LoginAsyncTask extends AsyncTask<Object,Object,UserCenter.LoginMessage>{
        String username;
        String password;
        boolean ioException = false;
        boolean jsonException = false;

        public LoginAsyncTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected UserCenter.LoginMessage doInBackground(Object[] params) {
            UserCenter.LoginMessage loginMessage = null;
            try {
                loginMessage = UserCenter.getInstance().login(username,password);
            }catch (IOException e){
                ioException = true;
            }catch (JSONException e){
                jsonException = true;
            }

            return loginMessage;
        }

        @Override
        protected void onPostExecute(UserCenter.LoginMessage loginMessage) {
            if(ioException){
                Toast.makeText(LoginActivity.this,"网络不佳",Toast.LENGTH_SHORT).show();
                return;
            }
            if(jsonException){
                Toast.makeText(LoginActivity.this,"json数据有误",Toast.LENGTH_SHORT).show();
                return;
            }

            if(loginMessage.isSuccess()){
                UserCenter.getInstance().storeUser(LoginActivity.this, loginMessage.getUser());
                UserCenter.getInstance().setUser(loginMessage.getUser());
                Toast.makeText(LoginActivity.this, "登录成功" + loginMessage.getCode(), Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                showLoginSuccess();
                login.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1500);
            }else {
                Toast.makeText(LoginActivity.this,loginMessage.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }


}
