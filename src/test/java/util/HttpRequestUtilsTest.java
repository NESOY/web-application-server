package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils.Pair;
import webserver.RequestHandler;

public class HttpRequestUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is("password2"));
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters.isEmpty(), is(true));
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined"), is("true"));
        assertThat(parameters.get("JSessionId"), is("1234"));
        assertThat(parameters.get("session"), is(nullValue()));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair, is(new Pair("userId", "javajigi")));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair, is(nullValue()));
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair, is(new Pair("Content-Length", "59")));
    }


    @Test
    public void parseURLTest() {
        //given
        String header = "GET /index.html HTTP/1.1";
        //when
        String url = HttpRequestUtils.parseResourcePathURL(header);
        //then
        assertEquals("/index.html", url);

        //given
        header = "GET /user/form.html HTTP/1.1";
        //when
        url = HttpRequestUtils.parseResourcePathURL(header);
        //then
        assertEquals("/user/form.html", url);


        //given
        header = "GET /user/create?userId=nesoy&password=password&name=youngjae HTTP/1.1";
        //when
        url = HttpRequestUtils.parseResourcePathURL(header);
        //then
        assertEquals("/user/create", url);
    }

    @Test
    public void parseUserInfoMapTest(){
        //given
        String header = "GET /user/create?userId=nesoy&password=password&name=youngjae&email=kyoje11@gmail.com HTTP/1.1";
        //when
        String url = HttpRequestUtils.parseResourceURL(header);
        Map parsedMap = HttpRequestUtils.parseQueryMap(url);

        //then
        assertEquals("nesoy",parsedMap.get("userId"));
        assertEquals("password",parsedMap.get("password"));
        assertEquals("youngjae",parsedMap.get("name"));
        assertEquals("kyoje11@gmail.com",parsedMap.get("email"));

        //given
        header = "GET /user/create HTTP/1.1";

        //when
        url = HttpRequestUtils.parseResourceURL(header);
        parsedMap = HttpRequestUtils.parseQueryMap(url);

        //then
        assertEquals(null,parsedMap);
    }
}
