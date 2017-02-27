package fido.api;

/**
 * Various constants for access FIDO server.
 */
public final class ApiConstants {

    public static final String CONTENT_TYPE = "application/fido+uaf; charset=utf-8";

    public static final String CONTENT_TYPE_TRUSTED_APPS = "application/fido.trusted-apps+json";

    public static final String CONTENT_TYPE_TEXT = "text/plain";

    public static final String CONTENT_TYPE_IMAGE = "image/png";

    public static final int IMAGE_WIDTH = 200;

    public static final int IMAGE_HEIGHT = 300;

    public static final String EMPTY_BODY = "";

    public static final int HTTP_CODE_BAD_REQUEST = 400;

    public static final int HTTP_CODE_INTERNAL_SERVER_ERROR = 500;
    
    public static final int UAF_STATUS_CODE_OK = 1200;
}
