package com.ihongqiqu.request.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ihongqiqu.request.Error;
import com.ihongqiqu.request.RequestAgent;
import com.ihongqiqu.request.RequestBuilder;
import com.ihongqiqu.request.Success;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView mRequest;
    private TextView mResponse;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestAgent.showLog();
        RequestAgent.init(getApplicationContext(), "https://api.github.com/");

        mTextMessage = (TextView) findViewById(R.id.message);
        mRequest = (TextView) findViewById(R.id.tv_request);
        mResponse = (TextView) findViewById(R.id.tv_response);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get:
                mResponse.setText("");
                testGet();
                break;
            case R.id.btn_post:
                mResponse.setText("");
                testPost();
                break;
        }
    }

    public void testGet() {


        RequestAgent.addHeader("Cookie", "123456");
        RequestAgent.addParam("publicKey", "publicValue");

        new RequestBuilder().path("users/jingle1267/repos")
                .success(new Success() {
                    @Override
                    public void onSuccess(String model) {

                        Log.e("MainActivity", "111");
                        mResponse.setText(model);
                    }
                })
                .error(new Error() {
                    @Override
                    public void onError(int statusCode, String errorMessage, Throwable t) {
                        Log.e("MainActivity", "222");
                        mResponse.setText(errorMessage);
                    }
                })
                .type("get")
                .build();

    }

    public void testPost() {

        RequestAgent.addHeader("Cookie", "123456");
        RequestAgent.addParam("publicKey", "publicValue");

        new RequestBuilder().path("users/jingle1267/repos")
                .header("head1", "head1Value")
                .param("param1", "param1Value")
                .success(new Success() {
                    @Override
                    public void onSuccess(String model) {

                        Log.e("MainActivity", "111");
                        mResponse.setText(model);
                    }
                })
                .error(new Error() {
                    @Override
                    public void onError(int statusCode, String errorMessage, Throwable t) {
                        Log.e("MainActivity", "222");
                        mResponse.setText(errorMessage);
                    }
                })
                .build();

    }

}
