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

        mTextMessage = (TextView) findViewById(R.id.message);
        mRequest = (TextView) findViewById(R.id.tv_request);
        mResponse = (TextView) findViewById(R.id.tv_response);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request:
                testRequest();
                break;
        }
    }

    public void testRequest() {
        RequestAgent.init(getApplicationContext(), "https://api.github.com/");

        new RequestBuilder().url("users/jingle1267/repos")
                .success(new Success() {
                    @Override
                    public void onSuccess(String model) {

                        Log.e("MainActivity", "111");
                        Log.e("MainActivity", model);
                        mResponse.setText(model);
                    }
                })
                .error(new Error() {
                    @Override
                    public void onError(int statusCode, String errorMessage, Throwable t) {
                        Log.e("MainActivity", "222");
                        Log.e("MainActivity", errorMessage);
                        mResponse.setText(errorMessage);
                    }
                })
                .get();

    }

}
