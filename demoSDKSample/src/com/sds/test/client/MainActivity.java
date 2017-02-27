package com.sds.test.client;

import java.security.MessageDigest;

import com.nice.client.R;

import android.app.*;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int sGetDiscoveryRequestType = 1;

    public static final int sRegistrationRequestType = 2;

    public static final int sAuthenticationRequestType = 3;

    public static final int sTransactionRequestType = 4;

    public static final int sDeregistrationRequestType = 5;

    public static final int sGetDiscoveryCallback = 6;

    public static final int sRegistrationCallback = 7;

    public static final int sAuthenticationCallback = 8;

    public static final int sTransactionCallback = 9;

    public static final int sDeregistrationCallback = 10;

    public static String mRequestUri = "https://demo.ez-auth.net/demo/InternalOnly/uaf/request";//"https://www.namecheck.co.kr/uaf/request.nc";//"https://trp.oxygenapi.com/rp2/uaf/request";

    public static String mResponseUri = "https://demo.ez-auth.net/demo/InternalOnly/uaf/response";//https://www.namecheck.co.kr/uaf/response.nc";//"https://trp.oxygenapi.com/rp2/uaf/response";
    
    //public static String mRequestUri = "https://sctest.oxygenapi.com/demorp/trp/uaf/request";

    //public static String mResponseUri = "https://sctest.oxygenapi.com/demorp/trp/uaf/response";

    public static String mUserName = "jane";

    private TextView mResultTextView;

	private TextView mSubtitle1;

	private TextView mSubtitle2;

	private TextView mSubtitle3;

	private EditText mInput1;

	private EditText mInput2;

	private EditText mInput3;

	private Button mButton1;

	private Button mButton2;

	private Button mButton3;

	private Button mButton4;

	private Button mButton5;

	private Button mButton6;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAppKeyHash();
        mResultTextView = (TextView) findViewById(R.id.textView_output);
        
		mSubtitle1 = (TextView) findViewById(R.id.popup_subTitle1);
		mSubtitle2 = (TextView) findViewById(R.id.popup_subTitle2);
		mSubtitle3 = (TextView) findViewById(R.id.popup_subTitle3);

		mInput1 = (EditText) findViewById(R.id.popup_getString1);
		mInput2 = (EditText) findViewById(R.id.popup_getString2);
		mInput3 = (EditText) findViewById(R.id.popup_getString3);

		mButton1 = (Button) findViewById(R.id.textView_initialize);

		mSubtitle1.setText("Request URI: ");
		mSubtitle2.setText("Response URI: ");
		mSubtitle3.setText("User Name:  ");

		mInput1.setText(mRequestUri);
		mInput2.setText(mResponseUri);
		mInput3.setText(mUserName);

		mButton2 = (Button) findViewById(R.id.textView_getDiscovery);
		mButton3 = (Button) findViewById(R.id.textView_registration);
		mButton4 = (Button) findViewById(R.id.textView_authentication);
		mButton5 = (Button) findViewById(R.id.textView_transaction);
		mButton6 = (Button) findViewById(R.id.textView_deregistration);
		
    }
    
    
    private void getAppKeyHash() { 
    	try { 
    		PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES); 
    		for (Signature signature : info.signatures) { 
    			MessageDigest md; md = MessageDigest.getInstance("SHA"); 
    			md.update(signature.toByteArray()); 
    			String something = new String(Base64.encode(md.digest(), 0)); 
    			Log.d("Hash key", something); 
    		} 
    	} catch (Exception e) { 
    		// TODO Auto-generated catch block 
    		Log.e("name not found", e.toString()); 
    	}
    }

    /**
     * Starts the selected test as connected native mode.
     *
     * @param view The selected view
     */
    public void startTest(View view) {

        switch (view.getId()) {
			case (R.id.textView_initialize):
	
				mRequestUri = mInput1.getText().toString().trim();
				mResponseUri = mInput2.getText().toString().trim();
				mUserName = mInput3.getText().toString().trim();
	
				mSubtitle1.setVisibility(View.GONE);
				mSubtitle2.setVisibility(View.GONE);
				mSubtitle3.setVisibility(View.GONE);
	
				mInput1.setVisibility(View.GONE);
				mInput2.setVisibility(View.GONE);
				mInput3.setVisibility(View.GONE);
	
				mButton1.setVisibility(View.GONE);
				mButton2.setVisibility(View.VISIBLE);
				mButton3.setVisibility(View.VISIBLE);
				mButton4.setVisibility(View.VISIBLE);
				mButton5.setVisibility(View.VISIBLE);
				mButton6.setVisibility(View.VISIBLE);
				
				mResultTextView.setVisibility(View.VISIBLE);
				break;
			
            case (R.id.textView_getDiscovery):
                Intent getDiscoveryIntent = new Intent(view.getContext(),
                        GetDiscoveryActivity.class);
            	getDiscoveryIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(getDiscoveryIntent);
                break;

            case (R.id.textView_registration):
                Intent registrationIntent = new Intent(view.getContext(),
                        RegistrationActivity.class);
                registrationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(registrationIntent);
                break;

            case (R.id.textView_authentication):
                Intent authenticationIntent = new Intent(view.getContext(),
                        AuthenticationActivity.class);
                authenticationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(authenticationIntent);
                break;

            case (R.id.textView_transaction):
                Intent transactioinIntent = new Intent(view.getContext(),
                        TransactionActivity.class);
                transactioinIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(transactioinIntent);
                break;

            case (R.id.textView_deregistration):
                Intent deregistrationIntent = new Intent(view.getContext(),
                        DeregistrationActivity.class);
                deregistrationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(deregistrationIntent);
                break;

            default:
                Log.d(TAG, "view.getId():" + view.getId() + " is not supported.");
                break;
        }
    }
}
