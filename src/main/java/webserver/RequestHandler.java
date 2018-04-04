package webserver;

import controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.HttpResponseUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static util.HttpResponseUtils.response200Header;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final String BASE_RESOURCE_URL = "./webapp";

	private Socket connection;

	private UserController userController = new UserController();

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			DataOutputStream dos = new DataOutputStream(out);

			String line = bufferedReader.readLine();

			String method = HttpRequestUtils.parseMethod(line);
			String resourcePath = HttpRequestUtils.parseResourcePath(line);

			if (method.equals("GET")) {
				routeGetMapping(resourcePath, line, bufferedReader, dos);
			}
			if (method.equals("POST")) {
				routePostMapping(resourcePath, line, bufferedReader, dos);
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void getRequestHandle(BufferedReader bufferedReader, DataOutputStream dos, String resourcePath, String line) throws IOException {
		byte[] body = Files.readAllBytes(new File(BASE_RESOURCE_URL + resourcePath).toPath());
		while (!"".equals(line)) {
			if (line == null) {
				return;
			}
			line = bufferedReader.readLine();
		}

		String response = response200Header(body.length);
		dos.writeBytes(response);
		responseBody(dos, body);
	}

	private String getPostQueryString(BufferedReader bufferedReader, String line) throws IOException {
		int size = 0;
		while (!"".equals(line)) {
			if (line == null) {
				return "";
			}

			HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
			if (pair != null) {
				if (pair.getKey().equals("Content-Length")) {
					size = Integer.parseInt(pair.getValue());

				}
			}

			line = bufferedReader.readLine();
		}

		return IOUtils.readData(bufferedReader, size);
	}

	private Map<String, String> getHeaderInfoMap(BufferedReader bufferedReader) throws IOException {
		Map<String, String> headerMap = new HashMap();
		String line = bufferedReader.readLine();

		while (!"".equals(line)) {
			HttpRequestUtils.Pair header = HttpRequestUtils.parseHeader(line);
			headerMap.put(header.getKey(), header.getValue());
			line = bufferedReader.readLine();
		}

		return headerMap;
	}

	private void routeGetMapping(String resourcePath, String line, BufferedReader bufferedReader, DataOutputStream dos) throws IOException {
		if (resourcePath.equals("/user/create")) {
			String query = HttpRequestUtils.parseQuery(line);
			Map<String, String> userInfoMap = HttpRequestUtils.parseQueryString(query);

			String response = userController.create(userInfoMap);

			dos.writeBytes(response);
			return;
		}

		if (resourcePath.equals("/user/list")) {
			Map<String, String> headerInfoMap = getHeaderInfoMap(bufferedReader);
			Map<String, String> cookieInfoMap = HttpRequestUtils.parseCookies(headerInfoMap.get("Cookie"));
			boolean isLogined = Boolean.parseBoolean(cookieInfoMap.get("logined"));

			if (isLogined) {
				String response = HttpResponseUtils.response302Header("/user/list.html");
				dos.writeBytes(response);
				return;
			}

			String response = HttpResponseUtils.response302Header("/user/login.html");
			dos.writeBytes(response);
			return;
		}

		getRequestHandle(bufferedReader, dos, resourcePath, line);
	}

	private void routePostMapping(String resourcePath, String line, BufferedReader bufferedReader, DataOutputStream dos) throws IOException {
		if (resourcePath.equals("/user/create")) {
			String data = getPostQueryString(bufferedReader, line);
			Map<String, String> userInfoMap = HttpRequestUtils.parseQueryString(data);

			String response = userController.create(userInfoMap);

			dos.writeBytes(response);
			return;
		}

		if (resourcePath.equals("/user/login")) {
			String data = getPostQueryString(bufferedReader, line);
			Map<String, String> userLoginInfo = HttpRequestUtils.parseQueryString(data);
			String userId = userLoginInfo.get("userId");
			String password = userLoginInfo.get("password");

			String response = userController.login(userId, password);

			dos.writeBytes(response);
			return;
		}
	}
}
