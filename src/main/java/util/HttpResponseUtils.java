package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseUtils.class);

    public static String response302Header(String redirectURL) {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 302 Found \r\n");
        response.append("Location: " + redirectURL + "\r\n");
        response.append("\r\n");

        return response.toString();
    }
}
