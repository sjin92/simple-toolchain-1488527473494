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

public class DeregistrationActivity extends Activity {

    private static final String TAG = DeregistrationActivity.class.getSimpleName();

    private TextView mResultTextView;
    
    private TextView mGetDeregistrationRequest, mResultRequestTextView, mSplit, mDrawline;
    
    private TextView mProcessDeregistrationRequest, mResultProcessDeregistrationRequest, mProcessDeregistrationRequestSplit, mProcessDeregistrationRequestDrawline;
    
    private long mBaseTime;
    
    private TextView mGetDeregistrationResponse, mResultGetDeregistrationResponsee, mGetDeregistrationResponseSplit, mGetDeregistrationResponseDrawline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deregistration);
        mResultTextView = (TextView) findViewById(R.id.textView_output);
        
        mGetDeregistrationRequest = (TextView) findViewById(R.id.getDeregistrationRequest);
        mResultRequestTextView = (TextView) findViewById(R.id.resultrequest);
        mSplit = (TextView)findViewById(R.id.split);
        mDrawline = (TextView) findViewById(R.id.drawline);
        
        mProcessDeregistrationRequest = (TextView) findViewById(R.id.processDeregistrationRequest);
        mResultProcessDeregistrationRequest = (TextView) findViewById(R.id.resultProcessDeregistrationRequest);
        mProcessDeregistrationRequestSplit = (TextView)findViewById(R.id.processDeregistrationRequestSplit);
        mProcessDeregistrationRequestDrawline = (TextView) findViewById(R.id.processDeregistrationRequestDrawline);   
        
        mGetDeregistrationResponse = (TextView) findViewById(R.id.getDeregistrationResponse);
        mResultGetDeregistrationResponsee = (TextView) findViewById(R.id.resultGetDeregistrationResponsee);
        mGetDeregistrationResponseSplit = (TextView)findViewById(R.id.getDeregistrationResponseSplit);
        mGetDeregistrationResponseDrawline = (TextView) findViewById(R.id.getDeregistrationResponseDrawline);         
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (RESULT_OK != resultCode) {
            mResultTextView.append("Deregistration complete. \n\n");
            return;
        }
        
        mResultProcessDeregistrationRequest.setText("호출결과: RESULT_OK");
		String sSplit = mSplit.getText().toString();
		sSplit = String.format("%s", getEllapse());
		mProcessDeregistrationRequestSplit.setText("경과시간: " + sSplit);
		Log.v(TAG, "UafClient.processDeregistrationRequest() 경과시간: " + sSplit);
		mProcessDeregistrationRequestDrawline.setText("--------------------------------------------------------------------------"); 
        
        new DeregistrationResponseAsyncTask()
                .execute(MainActivity.sDeregistrationCallback, this, data);
        
        mGetDeregistrationResponse.setText("호출함수: UafClient.getDeregistrationResponse()");
        mBaseTime = SystemClock.elapsedRealtime();
    }

    /**
     * Starts the selected test as connected native mode.
     *
     * @param view The selected view
     */
    public void startTest(View view) {
    	Log.v(TAG, "startTest");
    	mGetDeregistrationRequest.setText("호출함수: UafClient.getDeregistrationRequest()");

        if (view.getId() != R.id.deregistration_start) {
            Log.d(TAG, "Id:" + view.getId() + " is not supported.");
            return;
        }
        
		mBaseTime = SystemClock.elapsedRealtime();        
        new DeregistrationRequestAsyncTask().execute(MainActivity.sDeregistrationRequestType,
                this);
    }


    private class DeregistrationRequestAsyncTask extends AsyncTask<Object, Integer, Void> {

        int mRequestType;

        Activity mActivity;

        ReturnUafRequest mReturnUafRequest;

        @Override
        protected Void doInBackground(Object... params) {

            mRequestType = (Integer) params[0];

            mActivity = (Activity) params[1];

            Log.v(TAG, "DeregistrationRequestAsyncTask");
            if (mRequestType == MainActivity.sDeregistrationRequestType) {

                String context = "{\"userName\":" + "\"" + MainActivity.mUserName + "\"}";
                Log.v(TAG,
                        "Deregistration operation(getDeregistrationRequest) context is " + context);
                Log.v(TAG,
                        "Sends a request with given context to the given uri and then, returns a deregistration request message.");
                // Sends a request with given context to the given uri and then, returns a deregistration request message.
                mReturnUafRequest = UafClient.getDeregistrationRequest(MainActivity.mRequestUri,
                        context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        	
        	Log.v(TAG, "getDeregistrationRequest: onPostExecute");
        	
            if (null == mReturnUafRequest) {
                Log.d(TAG, "mReturnUafRequest is null.");
                mResultTextView.append("ReturnUafRequest is failed.");
                return;
            }

            // Returns the new UAF Request Message.
            String uafRequest = mReturnUafRequest.getUafRequest();
            Log.v(TAG, "Dereq.uafRequest is " + uafRequest);
            if (null == uafRequest) {
                Log.d(TAG, "uafRequest is null.");
                mResultTextView.append("getUafRequest is failed.");
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
	                                                              " - authenticators : " + "\n" +
	                                                              "   - aaid : " + mUafRequestParsing.getUafProtocolMessage().getAuthenticatorAaid() + "\n" +
	                                                              "   - keyID : " + mUafRequestParsing.getUafProtocolMessage().getAuthenticatorKeyID()
	                                                                                                  						);
	        } catch (Exception e) {
	        	Log.v(TAG, "error : " + e.getMessage());
	        	mResultRequestTextView.setText("ReturnUafRequest error");
	        	return;
			}
        
    		String sSplit = mSplit.getText().toString();
    		sSplit = String.format("%s", getEllapse());
    		mSplit.setText("경과시간: " + sSplit);
    		Log.v(TAG, "UafClient.getDeregistrationRequest() 경과시간: " + sSplit);
            mDrawline.setText("--------------------------------------------------------------------------"); 
            mProcessDeregistrationRequest.setText("호출함수: UafClient.processDeregistrationRequest()");
            mBaseTime = SystemClock.elapsedRealtime();

            // Executes a transaction operation of the given message.
            if (!UafClient
                    .processDeregistrationRequest(mActivity,
                            MainActivity.sDeregistrationRequestType, uafRequest)) {
                Log.d(TAG, "processDeregistrationRequest is failed.");
                mResultTextView.append("processDeregistrationRequest is failed. \n\n");
            } 
            //else {
            //    mResultTextView.append("processDeregistrationRequest is succeed. \n\n");
            //}
        }
    }

    private class DeregistrationResponseAsyncTask extends AsyncTask<Object, Integer, Void> {

        boolean mResult = false;

        @Override
        protected Void doInBackground(Object... params) {

            int requestType = (Integer) params[0];

            Intent data = (Intent) params[2];

            Log.v(TAG, "DeregistrationResponseAsyncTask");
            if (requestType == MainActivity.sDeregistrationCallback) {
                Log.v(TAG, "Returns the deregistration resultReturns the deregistration result.");
                // Returns the deregistration resultReturns the deregistration result
                mResult = UafClient.getDeregistrationResponse(data);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        	Log.v(TAG, "getDeregistrationResponse: onPostExecute");
        	
            // Returns the deregistration resultReturns the deregistration result
            Log.v(TAG, "Returns the deregistration resultReturns the deregistration result.");
            if (!mResult) {
                Log.d(TAG, "getDeregistrationResponse is failed.");
                mResultTextView.append("getDeregistrationResponse is failed. \n\n");
                return;
            }

            //mResultTextView.append("getDeregistrationResponse is succeed. \n\n");            
            mResultGetDeregistrationResponsee.setText("호출결과: success");
            String sSplit = mGetDeregistrationResponseSplit.getText().toString();
    		sSplit = String.format("%s", getEllapse());
    		mGetDeregistrationResponseSplit.setText("경과시간: " + sSplit);
    		Log.v(TAG, "UafClient.getDeregistrationResponse() 경과시간: " + sSplit);
    		mGetDeregistrationResponseDrawline.setText("--------------------------------------------------------------------------");
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
