package fido.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samsung.sds.fido.uaf.message.transport.HttpStatusCode;
import com.samsung.sds.fido.uaf.message.transport.context.RpContext;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final Map<String, RpContext> CONTEXT_CACHE = new HashMap<>();

    private static final Map<String, String> TRUSTED_FACETS_CACHE = new HashMap<>();

    private static final Map<String, List<String>> DEVICE_INFO_CACHE = new HashMap<>();

    private static final String FONT_ARIAL_BLACK = "Arial Black";
    
    private static final List<Pattern> passwordPatternList = new ArrayList<Pattern>();

    public static void putContext(String sessionId, RpContext context, int lifetimeMillis) {
        // TODO: use cache engine that support expiration time
        CONTEXT_CACHE.put(sessionId, context);
    }

    public static RpContext getContext(String sessionId) {
        return CONTEXT_CACHE.get(sessionId);
    }

    public static void removeContext(String sessionId) {
        CONTEXT_CACHE.remove(sessionId);
    }

    public static void putTrustedFacets(String rpId, String trustedFacets) {
        TRUSTED_FACETS_CACHE.put(rpId, trustedFacets);
    }

    public static String getTrustedFacets(String rpId) {
        return TRUSTED_FACETS_CACHE.get(rpId);
    }

    public static void removeTrustedFacets(String rpId) {
        TRUSTED_FACETS_CACHE.remove(rpId);
    }

    public static void putDeviceInfo(String mapKey, List<String> regIds) {
        DEVICE_INFO_CACHE.put(mapKey, regIds);
    }

    public static List getDeviceInfo(String mapKey) { return DEVICE_INFO_CACHE.get(mapKey); }

    public static void removeDeviceInfo(String mapKey) { DEVICE_INFO_CACHE.remove(mapKey); }

    /**
     * transactionData which received from client make to image.
     * @param transactionData
     * @param width     image size of width
     * @param height    image size of height
     * @return  bytes data
     */
    public static byte[] textToimage(String transactionData, int width, int height){
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.lightGray);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font(FONT_ARIAL_BLACK, Font.PLAIN, 14));
        drawString(graphics, transactionData, 10, 25);
        graphics.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytedImage = null;
        try {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            bytedImage = byteArrayOutputStream.toByteArray();
            LOGGER.debug("encoded image of byte array = ", bytedImage);
        }catch (IOException ioe){
            LOGGER.debug("ImageIo exception :{} ", ioe.getMessage());
            throw new RuntimeException("textToimage() Convert text to image");
        }

        LOGGER.trace("Image Created");
        return bytedImage;
    }

    private static void drawString(Graphics graphics, String text, int x, int y) {
        String[] texts = text.split("\n");
        if(texts.length > 0){
            for (String texInNewLine : texts) {
                LOGGER.trace("string draws in line : {}", texInNewLine);
                y += graphics.getFontMetrics().getHeight();
                graphics.drawString(texInNewLine, x, y);
            }
        }else{
            LOGGER.trace("string draws in line : {}", text);
            graphics.drawString(text, x, y);
        }
    }

    public static String getResult(HttpResponse httpResponse, HttpServletResponse servletResponse) {
        if (null == httpResponse) {
            servletResponse.setStatus(HttpStatusCode.HTTP_INTERNAL_SERVER_ERROR);
            return "";
        }

        int statusCode = httpResponse.getStatusCode();
        String body = httpResponse.getBody();

        // TODO: handle more HTTP statuc code as needed
        switch (statusCode) {
            case HttpStatusCode.HTTP_OK:
            case HttpStatusCode.HTTP_ACCEPTED:
                return body;
            default:
                servletResponse.setStatus(HttpStatusCode.HTTP_INTERNAL_SERVER_ERROR);
                return body;
        }
    }

    public static String getSearchKey(String userName, String deviceId){
        return userName + "$$" + deviceId;
    }

    public static void logRequest(Logger logger, HttpServletRequest request, String requestBody, String rpId, String apiKey) {
        String headerContentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        String clientUserAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String clientIp = request.getRemoteAddr();
        String bodyContent = null == headerContentType ? "" : new String(requestBody);
        
        List<Object> mList = new ArrayList<Object>();
        mList.add(clientIp);
        mList.add(headerContentType);
        mList.add(clientUserAgent);
        mList.add(bodyContent);
        mList.add(rpId);
        mList.add(apiKey);
        logger.trace(
        		"action=logRequest, clientIp={}, Content-Type=\"{}\", User-Agent=\"{}\", body=\"{}\" rp=\"{}\" rpKey=\"{}\"",
        		mList.toArray());
    }
    
    public static Map<String, String> convertJsonToMap(String jsonString){
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> resultMap = new HashMap<String, String>(); 
		
		if(jsonString == null || jsonString.isEmpty()){
			return resultMap;
		}
		
		try {
			resultMap = mapper.readValue(jsonString, new TypeReference<HashMap<String, String>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
    
}
