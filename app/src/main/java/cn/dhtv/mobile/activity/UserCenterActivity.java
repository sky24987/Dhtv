package cn.dhtv.mobile.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Favorite;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;

public class UserCenterActivity extends ActionBarActivity {
    private static final int REQUEST_CODE_LOGIN = 1;


    View mMyFact,mMyFavorite,mMyComment;
    private Button mLogout,mToFact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        mMyFact = findViewById(R.id.my_fact);
        mMyFavorite = findViewById(R.id.my_favorite);
        mMyComment = findViewById(R.id.my_comment);
        mLogout = (Button) findViewById(R.id.log_out);
        mToFact = (Button) findViewById(R.id.to_fact);
        mMyFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCenterActivity.this,FactActivity.class);
                startActivity(intent);
            }
        });
        mMyFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCenterActivity.this,FavoriteActivity.class);
                startActivity(intent);
            }
        });
        mMyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCenterActivity.this,CommentActivity.class);
                startActivity(intent);
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCenter.getInstance().logout(UserCenterActivity.this);
                finish();
            }
        });
        mToFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCenterActivity.this,SendFactActivity.class);
                startActivity(intent);
            }
        });

        User user = UserCenter.getInstance().retrieveUser(this);
        if(user == null){
            Intent intent = new Intent(UserCenterActivity.this,LoginActivity.class);
            startActivityForResult(intent,REQUEST_CODE_LOGIN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_center, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_LOGIN){
            if(resultCode == RESULT_OK){

            }
        }
    }
}
