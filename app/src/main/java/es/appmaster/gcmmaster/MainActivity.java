package es.appmaster.gcmmaster;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.appmaster.gcmmaster.api.RestClient;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String SENDER_ID = "400798164517";
    private static final String PREFERENCE_REG_ID = "REG_ID";
    private static final String PREFERENCE_NAME = "NOTIFICATIONS";

    private Button registerButton;
    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        gcm = GoogleCloudMessaging.getInstance(this);
    }

    private String getRegistrationIdFromGcmBlocking() throws IOException {

        String regId = gcm.register(SENDER_ID);
        Log.d("reg id", regId);

        // save in shared preferences
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_REG_ID, regId);
        editor.commit();

        return regId;
    }

    private void getRegistrationIdFromGcm() {
        // execute async task
        //new RegIdTask().execute("");
        RegIdTask regIdTask = new RegIdTask();
        regIdTask.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                //if ( !checkSharedPreferences() ) {
                getRegistrationIdFromGcm();
                /*} else {
                    Toast.makeText(this, "REG ID FROM SHARED PREFERENCES", Toast.LENGTH_SHORT).show();
                }*/
                break;
        }
    }

    private boolean checkSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String regId = preferences.getString(PREFERENCE_REG_ID, "");

        if (regId.equals("")) {
            return false;
        }

        return true;
    }

    private class RegIdTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                String regId = getRegistrationIdFromGcmBlocking();

                List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair("regId", regId));

                RestClient restClient = new RestClient();
                restClient.post("/register", postParams);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("reg id", result);
        }
    }

}
