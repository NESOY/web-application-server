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

	public static String response200Header(String resourcePath, int lengthOfBodyContent) {
		StringBuilder response = new StringBuilder();

		response.append("HTTP/1.1 200 OK \r\n");
		response.append(getContentType(resourcePath));
		response.append("Content-Length: " + lengthOfBodyContent + "\r\n");
		response.append("\r\n");

		return response.toString();
	}

	public static String loginSuccess() {
		StringBuilder response = new StringBuilder();

		response.append("HTTP/1.1 302 OK \r\n");
		response.append("Content-Type: text/html;charset=utf-8 \r\n");
		response.append("Set-Cookie: logined=true \r\n");
		response.append("Location: /index.html \r\n");
		response.append("\r\n");

		return response.toString();
	}

	public static String loginFail() {
		StringBuilder response = new StringBuilder();

		response.append("HTTP/1.1 302 OK \r\n");
		response.append("Content-Type: text/html;charset=utf-8 \r\n");
		response.append("Set-Cookie: logined=false \r\n");
		response.append("Location: /user/login_failed.html \r\n");
		response.append("\r\n");

		return response.toString();
	}

	public static String getContentType(String resourcePath) {
		String contentType = "Content-Type: ";
		if (resourcePath.endsWith(".html")) {
			contentType += "text/html";
		}
		else if (resourcePath.endsWith(".css")) {
			contentType += "text/css";
		}
		else if(resourcePath.endsWith(".woff2")){
			contentType += "font/woff2";
		}
		else if(resourcePath.endsWith(".js")){
			contentType += "text/javascript";
		}
		contentType += "\r\n";

		return contentType;
	}

}
