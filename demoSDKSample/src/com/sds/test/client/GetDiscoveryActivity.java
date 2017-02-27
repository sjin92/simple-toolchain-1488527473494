package com.sds.test.client;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.google.gson.*;
import com.nice.client.R;
import com.samsung.sds.fido.uaf.client.sdk.*;
import com.sds.sample.util.*;

public class GetDiscoveryActivity extends Activity {
	
    private static final String TAG = GetDiscoveryActivity.class.getSimpleName();

    private TextView mResultTextView;
    
    private TextView mDiscoverStart, mResultRequestTextView, mDrawline;
    
    private TextView mGetDiscoverResponseStart, mGetDiscoverResponseResult;
    
    private TextView mGetvender, mGetversion, mGetAuth;
    
    private long mBaseTime;
    
    private TextView mSplit, mSplit_Response;
    
    private DiscoveryDataParsing mDiscoveryDataParsing;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getdiscovery);
        mResultTextView = (TextView) findViewById(R.id.textView_output);
        
        mResultRequestTextView = (TextView) findViewById(R.id.resultrequest);
        mDiscoverStart = (TextView) findViewById(R.id.discoverstart);
        mSplit = (TextView)findViewById(R.id.split);
        mDrawline = (TextView) findViewById(R.id.drawline);
        
        mGetDiscoverResponseStart = (TextView) findViewById(R.id.getDiscoverResponseStart);
        mGetDiscoverResponseResult = (TextView) findViewById(R.id.getDiscoverResponseResult);
        mGetvender = (TextView) findViewById(R.id.getvender);
        mGetversion = (TextView) findViewById(R.id.getversion);
        mGetAuth = (TextView) findViewById(R.id.getAuth);
        mSplit_Response = (TextView)findViewById(R.id.split_Response);
    }

	/**
     * Starts the selected test as connected native mode.
     *
     * @param view The selected view
     */
    public void startTest(View view) {
    	mDiscoverStart.setText("호출함수: UafClient.discover()");
    	
        if (view.getId() != R.id.getdiscovery_start) {
            Log.d(TAG, "Id:" + view.getId() + " is not supported.");
            return;
        }
        
		mBaseTime = SystemClock.elapsedRealtime();		
        new GetDiscoveryRequestAsyncTask().execute(MainActivity.sGetDiscoveryRequestType, this);
    }
    
    private class GetDiscoveryRequestAsyncTask extends AsyncTask<Object, Integer, Void> {

        Activity mActivity;

        int mRequestType;

        @Override
        protected Void doInBackground(Object... params) {

            mRequestType = (Integer) params[0];

            mActivity = (Activity) params[1];

            if (mRequestType == MainActivity.sGetDiscoveryRequestType) {
        	            	
                // Executes a Discovery operation.
                Log.v(TAG, "Executes a Discovery operation.");
                if (!UafClient.discover(mActivity, MainActivity.sGetDiscoveryRequestType)) {
                    Log.d(TAG, "discover is failed.");
                }
            }
            return null;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         
        if (RESULT_OK != resultCode) {
            int errorCode = UafClient.getErrorCode(data);
            Log.d(TAG, "onActivityResult resultCode is not RESULT_OK. ErrorCode is: " + errorCode);

            mResultTextView.append("onActivityResult resultCode is not RESULT_OK. ErrorCode is: " + errorCode + "\n\n");
            return;
        }

        mResultRequestTextView.setText("호출결과: RESULT_OK");
		String sSplit = mSplit.getText().toString();
		sSplit = String.format("%s", getEllapse());
		mSplit.setText("경과시간: " + sSplit);
		Log.v(TAG, "UafClient.discover() 경과시간: " + sSplit);
        mDrawline.setText("--------------------------------------------------------------------------");
        
        new GetDiscoverResponseAsyncTask().execute(MainActivity.sGetDiscoveryCallback, this, data);
        
        mGetDiscoverResponseStart.setText("호출함수: UafClient.getDiscoverResponse()");
        mBaseTime = SystemClock.elapsedRealtime();
    }
    
    private class GetDiscoverResponseAsyncTask extends AsyncTask<Object, Integer, Void> {

        Activity mActivity;

        int mRequestType;

        Intent mData;

        @Override
        protected Void doInBackground(Object... params) {

            mRequestType = (Integer) params[0];

            mActivity = (Activity) params[1];

            mData = (Intent) params[2];

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (mRequestType == MainActivity.sGetDiscoveryCallback) {

                // Returns DiscoveryData.
                Log.v(TAG, "Returns DiscoveryData. Called getDiscoverResponse()");
                DiscoveryData discoveryData = UafClient.getDiscoverResponse(mData);
                if (null == discoveryData) {
                    Log.d(TAG, "getDiscoverResponse is failed.");
                    mResultTextView.append("getDiscoverResponse is failed. \n\n");
                    return;
                }
                Log.d(TAG, "DiscoveryData is " + discoveryData.toString());
                //mResultTextView.append(discoveryData.toString() + "\n\n");
     
                try {
	                mGetDiscoverResponseResult.setText("호출결과:\n1) supportedUAFVersions: " + discoveryData.getSupportedUAFVersionList());
	                mGetvender.setText("2) clientVendor: " + discoveryData.getClientVendor());
	                mGetversion.setText("3) clientVersion: " + discoveryData.getClientVersion());
	                
	                mDiscoveryDataParsing = new DiscoveryDataParsing(discoveryData);
	               
	                mGetAuth.setText("4) [availableAuthenticators]" + "\n" + 
	                					 " - Title: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getTitle() + "\n" +
	                					 " - AAID: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getAaid() + "\n" +
	                					 " - description: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getDescription() + "\n" +
	                					 " - assertionScheme: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getAssertionScheme()+ "\n" +
	                					 " - authenticationAlgorithm: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getAuthenticationAlgorithm() + "\n" +
	                					 " - userVerification: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getUserVerification() + "\n" +
	                					 " - keyProtection: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getKeyProtection() + "\n" +
	                					 " - matcherProtection: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getMatcherProtection() + "\n" +
	                					 " - attachmentHint: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getAttachmentHint() + "\n" +
	                					 " - isSecondFactorOnly: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getIsSecondFactorOnly()+ "\n" +
	                					 " - tcDisplay: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getTcDisplay() + "\n" +
	                					 " - tcDisplayContentType: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getTcDisplayContentType() + "\n" +      					 
	                					 //" - icon: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getIcon()+ "\n" +
	                					 " - imagePngContentType: " + mDiscoveryDataParsing.getAvailableAuthenticators().get(0).getImagePngContentType() + "\n"
	                												                                                                                );
	                Log.d(TAG, "mDiscoveryDataParsing is " + mDiscoveryDataParsing.getAvailableAuthenticators().toString());
                } catch (Exception e) {
                	Log.v(TAG, "error : " + e.getMessage());
                	mGetvender.setText("discoveryData error");
                	return;
    			}
                
        		String sSplit = mSplit_Response.getText().toString();
        		sSplit = String.format("%s", getEllapse());
        		mSplit_Response.setText("경과시간: " + sSplit);
        		Log.v(TAG, "UafClient.getDiscoverResponse() 경과시간: " + sSplit);
            } else {
                Log.d(TAG, "getDiscoverResponse is failed.");
                mResultTextView.append("getDiscoverResponse is failed. \n\n");
            }
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
