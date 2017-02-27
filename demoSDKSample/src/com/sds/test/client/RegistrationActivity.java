package com.sds.test.client;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.nice.client.R;
import com.samsung.sds.fido.uaf.client.sdk.*;
import com.sds.sample.util.*;

public class RegistrationActivity extends Activity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private static final String sRegistrationData = "RegistrationDataSample";

    private TextView mResultTextView;
    
    private TextView mRegistrationRequest, mResultRequestTextView, mSplit, mDrawline;
    
    private TextView mProcessRegistrationRequest, mResultProcessRegistrationRequest, mProcessRegistrationRequestSplit, mProcessRegistrationRequestDrawline;
    
    private long mBaseTime;
    
    private TextView mSendRegistrationResponse, mResultSendRegistrationResponse, mSendRegistrationResponseSplit, mSendRegistrationResponseDrawline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "TAG: " + TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mResultTextView = (TextView) findViewById(R.id.textView_output);
        
        mRegistrationRequest = (TextView) findViewById(R.id.registrationRequest);
        mResultRequestTextView = (TextView) findViewById(R.id.resultrequest);
        mSplit = (TextView)findViewById(R.id.split);
        mDrawline = (TextView) findViewById(R.id.drawline);
        
        mProcessRegistrationRequest = (TextView) findViewById(R.id.processRegistrationRequest);
        mResultProcessRegistrationRequest = (TextView) findViewById(R.id.resultProcessRegistrationRequest);
        mProcessRegistrationRequestSplit = (TextView)findViewById(R.id.processRegistrationRequestSplit);
        mProcessRegistrationRequestDrawline = (TextView) findViewById(R.id.processRegistrationRequestDrawline);        
        
        mSendRegistrationResponse = (TextView) findViewById(R.id.sendRegistrationResponse);
        mResultSendRegistrationResponse = (TextView) findViewById(R.id.resultSendRegistrationResponse);
        mSendRegistrationResponseSplit = (TextView)findViewById(R.id.sendRegistrationResponseSplit);
        mSendRegistrationResponseDrawline = (TextView) findViewById(R.id.sendRegistrationResponseDrawline);          
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult resultCode is : " + resultCode);
        if (RESULT_OK != resultCode) {
            int errorCode = UafClient.getErrorCode(data);
            Log.d(TAG, "onActivityResult resultCode is not RESULT_OK. ErrorCode is: " + errorCode);

            mResultTextView.append("onActivityResult resultCode is not RESULT_OK. ErrorCode is: " + errorCode + "\n\n");
            return;
        }
     
        mResultProcessRegistrationRequest.setText("호출결과: RESULT_OK");
		String sSplit = mSplit.getText().toString();
		sSplit = String.format("%s", getEllapse());
		mProcessRegistrationRequestSplit.setText("경과시간: " + sSplit + "(사용자 지문인식시간포함)");
		Log.v(TAG, "UafClient.processRegistrationRequest() 경과시간(사용자 지문인식시간포함): " + sSplit);
		mProcessRegistrationRequestDrawline.setText("--------------------------------------------------------------------------");

        new RegistrationResponseAsyncTask()
                .execute(MainActivity.sRegistrationCallback, this, data);
        
        mSendRegistrationResponse.setText("호출함수: UafClient.sendRegistrationResponse()");
        mBaseTime = SystemClock.elapsedRealtime();
    }

    /**
     * Starts the selected test as connected native mode.
     *
     * @param view The selected view
     */
    public void startTest(View view) {
    	mRegistrationRequest.setText("호출함수: UafClient.getRegistrationRequest()");

        if (view.getId() != R.id.registration_start) {
            Log.d(TAG, "Id:" + view.getId() + " is not supported.");
            return;
        }
        mBaseTime = SystemClock.elapsedRealtime();
        new RegistrationRequestAsyncTask().execute(MainActivity.sRegistrationRequestType,
                this);
    }


    private class RegistrationRequestAsyncTask extends AsyncTask<Object, Integer, Void> {

        int mRequestType;

        Activity mActivity;

        ReturnUafRequest mReturnUafRequest;

        @Override
        protected Void doInBackground(Object... params) {

            mRequestType = (Integer) params[0];

            mActivity = (Activity) params[1];

            String context = "{\"registrationData\":" + "\"" + sRegistrationData
                    + "\"" + ",\"userName\":" + "\"" + MainActivity.mUserName + "\"}";
            Log.v(TAG, "Registration operation(getRegistrationRequest) context is " + context);

            Log.v(TAG,
                    "Sends a request with given context to the given uri and then, returns a registration request message.");

            if (mRequestType == MainActivity.sRegistrationRequestType) {

                // Sends a request with given context to the given uri and then, returns a registration request message.
                mReturnUafRequest = UafClient
                        .getRegistrationRequest(MainActivity.mRequestUri, context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (null == mReturnUafRequest) {
                Log.d(TAG, "mReturnUafRequest is null.");
                mResultTextView.append("ReturnUafRequest is failed. \n\n");
                return;
            }
      
            // Returns the new UAF Request Message.
            String uafRequest = mReturnUafRequest.getUafRequest();
            Log.d(TAG, "Req.uafRequest is " + uafRequest);
            if (null == uafRequest) {
                Log.d(TAG, "uafRequest is null.");
                mResultTextView.append("getUafRequest is failed. \n\n");
                return;
            }
                       
            try {
            UafRequestParsing mUafRequestParsing = new UafRequestParsing(mReturnUafRequest);
            mResultRequestTextView.setText("호출결과 : [ReturnUafRequest]" + "\n" + 
            												  " - upv : major : " + mUafRequestParsing.getUafProtocolMessage().getUpv().getMajor() +  
            												       ", minor : " + mUafRequestParsing.getUafProtocolMessage().getUpv().getMinor() + "\n" +
                                                              " - op : " + mUafRequestParsing.getOp() + "\n" +
                                                              " - Appid : " + mUafRequestParsing.getUafProtocolMessage().getAppid() + "\n" +     
                                                              " - serverData : " + mUafRequestParsing.getUafProtocolMessage().getServerdata() + "\n" +
                                                              " - challenge : " + mUafRequestParsing.getUafProtocolMessage().getChallenge() + "\n" +
                                                              " - username : " + mUafRequestParsing.getUafProtocolMessage().getUsername()
                                                              //" - policy : " + mUafRequestParsing.getPolicyAaid() + "\n"                                                              
                                                                                                  );     
            } catch (Exception e) {
            	Log.v(TAG, "error : " + e.getMessage());
            	mResultRequestTextView.setText("ReturnUafRequest error");
            	return;
			}
            
    		String sSplit = mSplit.getText().toString();
    		sSplit = String.format("%s", getEllapse());
    		mSplit.setText("경과시간: " + sSplit);
    		Log.v(TAG, "UafClient.getRegistrationRequest() 경과시간: " + sSplit);
            mDrawline.setText("--------------------------------------------------------------------------");
            mProcessRegistrationRequest.setText("호출함수: UafClient.processRegistrationRequest()");
            mBaseTime = SystemClock.elapsedRealtime();

            // Executes a registration operation of the given message.
            if (!UafClient
                    .processRegistrationRequest(mActivity, MainActivity.sRegistrationRequestType,
                            uafRequest)) {
                Log.d(TAG, "processRegistrationRequest is failed.");
                mResultTextView.append("processRegistrationRequest is failed. \n\n");
            } 
            //else {
            //    mResultTextView.append("processRegistrationRequest is succeed. \n\n");
            //}
        }
    }

    private class RegistrationResponseAsyncTask extends AsyncTask<Object, Integer, Void> {

        ServerResponse mServerResponse;

        @Override
        protected Void doInBackground(Object... params) {

            int requestType = (Integer) params[0];

            Activity activity = (Activity) params[1];

            Intent data = (Intent) params[2];

            String context = "{\"registrationData\":" + "\"" + sRegistrationData
                    + "\"" + ",\"userName\":" + "\"" + MainActivity.mUserName + "\"}";
            Log.v(TAG, "Registration operation(sendRegistrationResponse) context is " + context);

            if (requestType == MainActivity.sRegistrationCallback) {

                Log.v(TAG, "Returns the ServerResponse from given data.");
                // Returns the ServerResponse from given data.
                mServerResponse = UafClient
                        .sendRegistrationResponse(data, MainActivity.mResponseUri, context,
                                activity);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (null == mServerResponse) {
                Log.d(TAG, "sendRegistrationResponse is failed.");
                mResultTextView.append("sendRegistrationResponse is failed. \n\n");
                return;
            }

            // Returns a string containing a concise, human-readable description of this object.
            //mResultTextView.append(mServerResponse.toString() + "\n\n");            
            mResultSendRegistrationResponse.setText("호출결과:\n1) mServerResponse: " + mServerResponse.toString());
            String sSplit = mSendRegistrationResponseSplit.getText().toString();
    		sSplit = String.format("%s", getEllapse());
    		mSendRegistrationResponseSplit.setText("경과시간: " + sSplit);
    		Log.v(TAG, "UafClient.sendRegistrationResponse() 경과시간: " + sSplit);
    		mSendRegistrationResponseDrawline.setText("--------------------------------------------------------------------------");
        }
    }
    
	String getEllapse() {
		long now = SystemClock.elapsedRealtime();
		long ell = now - mBaseTime;
		String sEll = String.format("%02d:%02d:%02d", ell / 1000 / 60, 
				(ell / 1000) % 60, (ell % 1000) / 10);
		return sEll;
	}    
}
