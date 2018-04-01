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

    public static String response200Header(int lengthOfBodyContent) {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 200 OK \r\n");
//            response.append("Content-Type: text/html;charset=utf-8\r\n"); //Todo Resource에 맞게 Content-Type 보내기.
        response.append("Content-Length: " + lengthOfBodyContent + "\r\n");
        response.append("\r\n");

        return response.toString();
    }
}
