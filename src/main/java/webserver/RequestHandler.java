package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String BASE_RESOURCE_URL = "./webapp";

    private Socket connection;

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
                getRequestHandle(bufferedReader, dos, resourcePath, line);
            }
            if (method.equals("POST")) {
                postRequestHandle(bufferedReader, dos, line);
            }



        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n"); //Todo Resource에 맞게 Content-Type 보내기.
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectURL) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectURL + "\r\n");
            dos.writeBytes("\r\n");

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

    private void postRequestHandle(BufferedReader bufferedReader, DataOutputStream dos, String line) throws IOException {
        int size = 0;
        while (!"".equals(line)) {
            if (line == null) {
                return;
            }

            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            if (pair != null) {
                if (pair.getKey().equals("Content-Length")) {
                    size = Integer.parseInt(pair.getValue());

                }
            }

            line = bufferedReader.readLine();
        }

        String data = IOUtils.readData(bufferedReader, size);

        Map<String, String> userInfoMap = HttpRequestUtils.parseQueryString(data);
        User user = User.getUserInstance(userInfoMap);
        DataBase.addUser(user);
        response302Header(dos, "/index.html");
    }

    private void getRequestHandle(BufferedReader bufferedReader, DataOutputStream dos, String resourcePath, String line) throws IOException {
        if (resourcePath.equals("/user/create")) {
            String query = HttpRequestUtils.parseQuery(line);
            Map<String, String> userInfoMap = HttpRequestUtils.parseQueryString(query);
            User user = User.getUserInstance(userInfoMap);
            DataBase.addUser(user);
            response302Header(dos, "/index.html");
            return;
        }

        byte[] body = Files.readAllBytes(new File(BASE_RESOURCE_URL + resourcePath).toPath());
        while (!"".equals(line)) {
            if (line == null) {
                return;
            }
            line = bufferedReader.readLine();
        }

        response200Header(dos, body.length);
        responseBody(dos, body);
    }
}
