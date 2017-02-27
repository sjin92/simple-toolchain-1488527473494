package com.sds.sample.util;

import java.util.*;

import android.util.*;

import com.google.gson.*;
import com.samsung.sds.fido.uaf.client.sdk.*;

public class DiscoveryDataParsing {

    private static final String TAG = DiscoveryDataParsing.class.getSimpleName();

    private String clientVendor;
    private ArrayList<DiscoveryDataItem> availableAuthenticators;
    private List<Version> supportedUAFVersions;

	
    public DiscoveryDataParsing(DiscoveryData discoveryData) {
    
	Gson gson = new Gson();
    String json = gson.toJson(discoveryData);
    clientVendor = discoveryData.getClientVendor();
    supportedUAFVersions = discoveryData.getSupportedUAFVersionList();
    availableAuthenticators = new ArrayList<DiscoveryDataItem>();

    try {
        JsonObject jsobject = gson.fromJson(json, JsonObject.class);
        JsonObject jsonDiscoveryData = jsobject.getAsJsonObject("discoveryData");
        JsonArray availableAuthenticatorList = jsonDiscoveryData.getAsJsonArray("availableAuthenticators");

        for (int i = 0; i < availableAuthenticatorList.size(); i++) {

            JsonObject object = availableAuthenticatorList.get(i).getAsJsonObject();
            DiscoveryDataItem authenticator = new DiscoveryDataItem();

            authenticator.title = object.get("title").getAsString();
            authenticator.aaid = object.get("aaid").getAsString();
            authenticator.description = object.get("description").getAsString();
            authenticator.assertionScheme = object.get("assertionScheme").getAsString();            
            authenticator.authenticationAlgorithm = parseAuthenticationAlgorithmData(object.get("authenticationAlgorithm").getAsShort());
            authenticator.userVerification = parseUserVerificationData(object.get("userVerification").getAsInt());
            authenticator.keyProtection = parseKeyProtectionData(object.get("keyProtection").getAsShort());
            authenticator.matcherProtection = parseMatcherProtectionData(object.get("matcherProtection").getAsShort());
            authenticator.attachmentHint = parseAttachmentHintData(object.get("attachmentHint").getAsInt());
            authenticator.isSecondFactorOnly = object.get("isSecondFactorOnly").getAsBoolean();
            authenticator.tcDisplay = parseTcDisplayData(object.get("tcDisplay").getAsShort());
            authenticator.tcDisplayContentType = object.get("tcDisplayContentType").getAsString();
            authenticator.icon = object.get("icon").getAsString();
            authenticator.imagePngContentType = object.get("imagePngContentType").getAsString();

            //supportedUAFVersionsJsonArray = object.get("supportedUAFVersions").getAsJsonArray();
            availableAuthenticators.add(authenticator);
        }
	    } catch (JsonSyntaxException e) {
	        Log.v(TAG, "JsonSyntax error : " + e.getMessage());
	
	    } catch (JsonParseException e) {
	        Log.v(TAG, "JsonParse error : " + e.getMessage());
	    }
    }

    public ArrayList<DiscoveryDataItem> getAvailableAuthenticators() {
        return availableAuthenticators;
    }          
    
    private String parseUserVerificationData(Integer userVerification) {

        String userVerificationResult = ""; // (";

        if (0x01 == (userVerification & 0x01)) {
            userVerificationResult += " USER_VERIFY_PRESENCE";
        }
        if (0x02 == (userVerification & 0x02)) {
            userVerificationResult += " USER_VERIFY_FINGERPRINT";
        }
        if (0x04 == (userVerification & 0x04)) {
            userVerificationResult += " USER_VERIFY_PASSCODE";
        }
        if (0x08 == (userVerification & 0x08)) {
            userVerificationResult += " USER_VERIFY_VOICEPRINT";
        }
        if (0x10 == (userVerification & 0x10)) {
            userVerificationResult += " USER_VERIFY_FACEPRINT";
        }
        if (0x20 == (userVerification & 0x20)) {
            userVerificationResult += " USER_VERIFY_LOCATION";
        }
        if (0x40 == (userVerification & 0x40)) {
            userVerificationResult += " USER_VERIFY_EYEPRINT";
        }
        if (0x80 == (userVerification & 0x80)) {
            userVerificationResult += " USER_VERIFY_PATTERN";
        }
        if (0x100 == (userVerification & 0x100)) {
            userVerificationResult += " USER_VERIFY_HANDPRINT";
        }
        if (0x200 == (userVerification & 0x200)) {
            userVerificationResult += " USER_VERIFY_NONE";
        }
        if (0x400 == (userVerification & 0x400)) {
            userVerificationResult += " USER_VERIFY_ALL";
        }

        //userVerificationResult += " )";
        return userVerificationResult;
    }

    private String parseMatcherProtectionData(Short matcherProtection) {

        String matcherProtectionResult = ""; // (";

        if (0x01 == (matcherProtection & 0x01)) {
            matcherProtectionResult += " MATCHER_PROTECTION_SOFTWARE";
        }
        if (0x02 == (matcherProtection & 0x02)) {
            matcherProtectionResult += " MATCHER_PROTECTION_TEE";
        }
        if (0x04 == (matcherProtection & 0x04)) {
            matcherProtectionResult += " MATCHER_PROTECTION_ON_CHIP";
        }

        //matcherProtectionResult += " )";
        return matcherProtectionResult;
    }

    private String parseKeyProtectionData(Short keyProtection) {

        String keyProtectionResult = ""; // (";

        if (0x01 == (keyProtection & 0x01)) {
            keyProtectionResult += " KEY_PROTECTION_SOFTWARE";
        }
        if (0x02 == (keyProtection & 0x02)) {
            keyProtectionResult += " KEY_PROTECTION_HARDWARE";
        }
        if (0x04 == (keyProtection & 0x04)) {
            keyProtectionResult += " KEY_PROTECTION_TEE";
        }
        if (0x08 == (keyProtection & 0x08)) {
            keyProtectionResult += " KEY_PROTECTION_SECURE_ELEMENT";
        }
        if (0x10 == (keyProtection & 0x10)) {
            keyProtectionResult += " KEY_PROTECTION_REMOTE_HANDLE";
        }

        //keyProtectionResult += " )";
        return keyProtectionResult;
    }

    private String parseAttachmentHintData(Integer attachmentHint) {

        String attachmentHintResult = ""; //" (";

        if (0x01 == (attachmentHint & 0x01)) {
            attachmentHintResult += " ATTACHMENT_HINT_INTERNAL";
        }
        if (0x02 == (attachmentHint & 0x02)) {
            attachmentHintResult += " ATTACHMENT_HINT_EXTERNAL";
        }
        if (0x04 == (attachmentHint & 0x04)) {
            attachmentHintResult += " ATTACHMENT_HINT_WIRED";
        }
        if (0x08 == (attachmentHint & 0x08)) {
            attachmentHintResult += " ATTACHMENT_HINT_WIRELESS";
        }
        if (0x10 == (attachmentHint & 0x10)) {
            attachmentHintResult += " ATTACHMENT_HINT_NFC";
        }
        if (0x20 == (attachmentHint & 0x20)) {
            attachmentHintResult += " ATTACHMENT_HINT_BLUETOOTH";
        }
        if (0x40 == (attachmentHint & 0x40)) {
            attachmentHintResult += " ATTACHMENT_HINT_NETWORK";
        }
        if (0x80 == (attachmentHint & 0x80)) {
            attachmentHintResult += " ATTACHMENT_HINT_READY";
        }
        if (0x100 == (attachmentHint & 0x80)) {
            attachmentHintResult += " ATTACHMENT_HINT_WIFI_DIRECT";
        }

        //attachmentHintResult += " )";
        return attachmentHintResult;
    }

    private String parseAuthenticationAlgorithmData(Short authenticationAlgorithm) {

        String authenticationAlgorithmResult = ""; //= " \n(";

        switch (authenticationAlgorithm) {

            case (0x01):

                authenticationAlgorithmResult += " UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_RAW";
                break;

            case (0x02):

                authenticationAlgorithmResult += " UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_DER";
                break;

            case (0x03):

                authenticationAlgorithmResult += " UAF_ALG_SIGN_RSASSA_PSS_SHA256_RAW";
                break;

            case (0x04):

                authenticationAlgorithmResult += " UAF_ALG_SIGN_RSASSA_PSS_SHA256_DER";
                break;

            case (0x05):

                authenticationAlgorithmResult += " UAF_ALG_SIGN_SECP256K1_ECDSA_SHA256_RAW";
                break;

            case (0x06):

                authenticationAlgorithmResult += " UAF_ALG_SIGN_SECP256K1_ECDSA_SHA256_DER";
                break;
        }

        //authenticationAlgorithmResult += " )";
        return authenticationAlgorithmResult;
    }

    private String parseTcDisplayData(Short tcDisplay) {

        String tcDisplayResult = ""; // (";

        if (0x01 == (tcDisplay & 0x01)) {
            tcDisplayResult += " TRANSACTION_CONFIRMATION_DISPLAY_ANY";
        }
        if (0x02 == (tcDisplay & 0x02)) {
            tcDisplayResult += " TRANSACTION_CONFIRMATION_DISPLAY_PRIVILEGED_SOFTWARE";
        }
        if (0x04 == (tcDisplay & 0x04)) {
            tcDisplayResult += " TRANSACTION_CONFIRMATION_DISPLAY_TEE";
        }
        if (0x08 == (tcDisplay & 0x08)) {
            tcDisplayResult += " TRANSACTION_CONFIRMATION_DISPLAY_HARDWARE";
        }
        if (0x10 == (tcDisplay & 0x10)) {
            tcDisplayResult += " TRANSACTION_CONFIRMATION_DISPLAY_REMOTE";
        }

        //tcDisplayResult += " )";
        return tcDisplayResult;
    }
    
}

