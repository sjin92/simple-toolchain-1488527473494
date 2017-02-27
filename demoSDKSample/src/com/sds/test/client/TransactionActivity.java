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


public class TransactionActivity extends Activity {

    private static final String TAG = TransactionActivity.class.getSimpleName();

    private static final String sTransactionData = "TransactionConfirmationDataSample";

    private TextView mResultTextView;
    
    private TextView mAuthenticationRequest, mResultRequestTextView, mSplit, mDrawline;
    
    private TextView mProcessAuthenticationRequest, mResultProcessAuthenticationRequest, mProcessAuthenticationRequestSplit, mProcessAuthenticationRequestDrawline;
    
    private long mBaseTime;
    
    private TextView mSendAuthenticationResponse, mResultSendAuthenticationResponse, mSendAuthenticationResponseSplit, mSendAuthenticationResponseDrawline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        mResultTextView = (TextView) findViewById(R.id.textView_output);
        
        mAuthenticationRequest = (TextView) findViewById(R.id.authenticationRequest);
        mResultRequestTextView = (TextView) findViewById(R.id.resultrequest);
        mSplit = (TextView)findViewById(R.id.split);
        mDrawline = (TextView) findViewById(R.id.drawline);
        
        mProcessAuthenticationRequest = (TextView) findViewById(R.id.processAuthenticationRequest);
        mResultProcessAuthenticationRequest = (TextView) findViewById(R.id.resultProcessAuthenticationRequest);
        mProcessAuthenticationRequestSplit = (TextView)findViewById(R.id.processAuthenticationRequestSplit);
        mProcessAuthenticationRequestDrawline = (TextView) findViewById(R.id.processAuthenticationRequestDrawline);     
        
        mSendAuthenticationResponse = (TextView) findViewById(R.id.sendAuthenticationResponse);
        mResultSendAuthenticationResponse = (TextView) findViewById(R.id.resultSendAuthenticationResponse);
        mSendAuthenticationResponseSplit = (TextView)findViewById(R.id.sendAuthenticationResponseSplit);
        mSendAuthenticationResponseDrawline = (TextView) findViewById(R.id.sendAuthenticationResponseDrawline); 
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK != resultCode) {
            int errorCode = UafClient.getErrorCode(data);
            Log.d(TAG, "onActivityResult resultCode is not RESULT_OK. ErrorCode is: " + errorCode);

            mResultTextView.append("onActivityResult resultCode is not RESULT_OK. ErrorCode is: " + errorCode +"\n\n");
            return;
        }

        mResultProcessAuthenticationRequest.setText("호출결과: RESULT_OK");
		String sSplit = mSplit.getText().toString();
		sSplit = String.format("%s", getEllapse());
		mProcessAuthenticationRequestSplit.setText("경과시간: " + sSplit + "(사용자 지문인식시간포함)");
		Log.v(TAG, "UafClient.processAuthenticationRequest() 경과시간(사용자 지문인식시간포함): " + sSplit);
		mProcessAuthenticationRequestDrawline.setText("--------------------------------------------------------------------------"); 
        
        new TransactionResponseAsyncTask()
                .execute(MainActivity.sTransactionCallback, this, data);
        
        mSendAuthenticationResponse.setText("호출함수: UafClient.sendAuthenticationResponse()");
        mBaseTime = SystemClock.elapsedRealtime();
    }

    /**
     * Starts the selected test as connected native mode.
     *
     * @param view The selected view
     */
    public void startTest(View view) {
    	mAuthenticationRequest.setText("호출함수: UafClient.getAuthenticationRequest()");
        if (view.getId() != R.id.transaction_start) {
            Log.d(TAG, "Id:" + view.getId() + " is not supported.");
            return;
        }
        mBaseTime = SystemClock.elapsedRealtime();
        new TransactionRequestAsyncTask().execute(MainActivity.sTransactionRequestType,
                this);
    }


    private class TransactionRequestAsyncTask extends AsyncTask<Object, Integer, Void> {

        int mRequestType;

        Activity mActivity;

        ReturnUafRequest mReturnUafRequest;

        @Override
        protected Void doInBackground(Object... params) {

            mRequestType = (Integer) params[0];

            mActivity = (Activity) params[1];

            if (mRequestType == MainActivity.sTransactionRequestType) {

                String context = "{\"transactionData\":" + "\"" + sTransactionData
                        + "\"" + ",\"userName\":" + "\"" + MainActivity.mUserName + "\"}";

                Log.v(TAG, "Transaction operation(getAuthenticationRequest) context is " + context);
                Log.v(TAG,
                        "Sends a request with given context to the given uri and then, returns a authentication or transaction confirmation request message.");

                // Sends a request with given context to the given uri and then, returns a authentication or transaction confirmation request message.
                mReturnUafRequest = UafClient
                        .getAuthenticationRequest(MainActivity.mRequestUri, context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (null == mReturnUafRequest) {
                Log.d(TAG, "mReturnUafRequest is null in transaction operation.");
                mResultTextView.append("ReturnUafRequest is failed in transaction operation. \n\n");
                return;
            }

            // Returns the new UAF Request Message.
            String uafRequest = mReturnUafRequest.getUafRequest();
            if (null == uafRequest) {
                Log.d(TAG, "uafRequest is null in transaction operation.");
                mResultTextView.append("getUafRequest is failed in transaction operation. \n\n");
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
                                                              " - challenge : " + mUafRequestParsing.getUafProtocolMessage().getChallenge()
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
    		Log.v(TAG, "UafClient.getAuthenticationRequest() 경과시간: " + sSplit);
            mDrawline.setText("--------------------------------------------------------------------------"); 
            mProcessAuthenticationRequest.setText("호출함수: UafClient.processAuthenticationRequest()");
            mBaseTime = SystemClock.elapsedRealtime();

            // Executes a transaction operation of the given message.
            if (!UafClient
                    .processAuthenticationRequest(mActivity, MainActivity.sTransactionRequestType,
                            uafRequest)) {
                Log.d(TAG, "processAuthenticationRequest is failed in transaction operation.");
                mResultTextView.append(
                        "processAuthenticationRequest is failed in transaction operation. \n\n");
            } 
            //else {
            //    mResultTextView.append(
            //            "processAuthenticationRequest is succeed in transaction operation. \n\n");
            //}
        }
    }

    private class TransactionResponseAsyncTask extends AsyncTask<Object, Integer, Void> {

        ServerResponse mServerResponse;

        @Override
        protected Void doInBackground(Object... params) {

            int requestType = (Integer) params[0];

            Activity activity = (Activity) params[1];

            Intent data = (Intent) params[2];

            if (requestType == MainActivity.sTransactionCallback) {

                String context = "{\"transactionData\":" + "\"" + sTransactionData
                        + "\"" + ",\"userName\":" + "\"" + MainActivity.mUserName + "\"}";

                Log.v(TAG,
                        "Transaction operation(sendAuthenticationResponse) context is " + context);
                Log.v(TAG, "Returns the ServerResponse from given data.");
                // Returns the ServerResponse from given data.
                mServerResponse = UafClient
                        .sendAuthenticationResponse(data, MainActivity.mResponseUri, context,
                                activity);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (null == mServerResponse) {
                Log.d(TAG, "sendAuthenticationResponse is failed in transaction operation.");
                mResultTextView
                        .append("sendAuthenticationResponse is failed in transaction operation. \n\n");
                return;
            }

            // Returns a string containing a concise, human-readable description of this object.
            //mResultTextView.append(mServerResponse.toString() + "\n\n");            
            mResultSendAuthenticationResponse.setText("호출결과:\n1) mServerResponse: " + mServerResponse.toString());
            String sSplit = mSendAuthenticationResponseSplit.getText().toString();
    		sSplit = String.format("%s", getEllapse());
    		mSendAuthenticationResponseSplit.setText("경과시간: " + sSplit);
    		Log.v(TAG, "UafClient.sendAuthenticationResponse() 경과시간: " + sSplit);
    		mSendAuthenticationResponseDrawline.setText("--------------------------------------------------------------------------");
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
