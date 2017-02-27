package com.sds.sample.util;

public class DiscoveryDataItem {

    String title = null;
    String aaid = null;
    String description = null;
    String assertionScheme = null;
    String authenticationAlgorithm = null;
    String userVerification = null;
    String keyProtection = null;
    String matcherProtection = null;
    String attachmentHint = null;
    Boolean isSecondFactorOnly = null;
    String tcDisplay = null;
    String tcDisplayContentType = null;
    String icon = null;
    String imagePngContentType = "image/png";

    //   private List<String> supportedExtensionIDs = null;
    //   private List<DisplayPngCharacteristicsDescriptor> tcDisplayPNGCharacteristics = null;
    //   private List<Short> attestationTypes = null;


    public String getTitle() {
        return title;
    }

    public String getAaid() {
        return aaid;
    }

    public String getDescription() {
        return description;
    }

    public String getAssertionScheme() {
        return assertionScheme;
    }

    public String getAuthenticationAlgorithm() {
        return authenticationAlgorithm;
    }

    public String getUserVerification() {
        return userVerification;
    }

    public String getKeyProtection() {
        return keyProtection;
    }

    public String getMatcherProtection() {
        return matcherProtection;
    }

    public String getAttachmentHint() {
        return attachmentHint;
    }

    public Boolean getIsSecondFactorOnly() {
        return isSecondFactorOnly;
    }

    public String getTcDisplay() {
        return tcDisplay;
    }

    public String getTcDisplayContentType() {
        return tcDisplayContentType;
    }

    public String getIcon() {
        return icon;
    }

    public String getImagePngContentType() {
        return imagePngContentType;
    }
	
}
