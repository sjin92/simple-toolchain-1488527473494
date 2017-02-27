package fido.api;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.util.properties.PropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("configurations")
public final class Configurations {
    private static final Logger LOGGER = LoggerFactory.getLogger(Configurations.class);

    private static final String API_URI = ".uri";

    private static final String API_KEY = ".key";

    private static final String MESSAGE_LIFETIMEMILLIS = "message.lifetimeMillis";
    
    private static final int DEFAULT_MESSAGE_LIFETIMEMILLIS = 30000;
    
    private static final String POLICY_TYPE = "policy.type.";

    @Inject
    @Named("propertiesService")
    protected PropertiesService propertiesService;

    private String getStringConfiguration(String key) {
        String value = propertiesService.getString(key);
        LOGGER.trace("action=getStringConfiguration, {}={}", key, value);

        if (value.isEmpty()) {
            value = null;
        }

        if (null == value) {
            LOGGER.error("action=getStringConfiguration, Cannot get {}", key);
        }

        return value;
    }

    private int getIntConfiguration(String key, int defaultValue) {
        int value = propertiesService.getInt(key, defaultValue);
        LOGGER.trace("action=getIntConfiguration, {}={}", key, value);
        return value;
    }

    public String getApiKey(String rpId) {
        return propertiesService.getString(rpId + API_KEY).replaceAll("\"", "");
    }

    public String getApiUri(String rpId) {
        return propertiesService.getString(rpId + API_URI).replaceAll("\"", "");
    }

    public int getLifetimeMillis() {
        return propertiesService.getInt(MESSAGE_LIFETIMEMILLIS, DEFAULT_MESSAGE_LIFETIMEMILLIS);
    }
    
    public String getPolicyId(String policyType) {
        return propertiesService.getString(POLICY_TYPE + policyType).replaceAll("\"", "");
    }
}
