package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.Map;

import static util.HttpResponseUtils.loginFail;
import static util.HttpResponseUtils.loginSuccess;
import static util.HttpResponseUtils.response302Header;

public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public String login(String userId, String password) {
        String response;
        boolean isSignedUser = DataBase.isSignedUser(userId, password);

        if (isSignedUser) {
            response = loginSuccess();
            log.debug("login success");
            return response;
        }

        response = loginFail();
        log.debug("login fail");
        return response;
    }

    public String create(Map<String, String> userInfoMap) {
        User user = User.getUserInstance(userInfoMap);

        DataBase.addUser(user);
        log.debug("user Create success");
        return response302Header("/index.html");
    }
}
