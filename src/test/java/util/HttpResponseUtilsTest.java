package util;

import org.junit.Assert;
import org.junit.Test;

public class HttpResponseUtilsTest {

    @Test
    public void response302HeaderTest() {
        //given
        String redirectURL = "nesoy.github.io";

        //when
        String response = HttpResponseUtils.response302Header(redirectURL);

        //then
        Assert.assertTrue(response.contains(redirectURL));
    }

    @Test
    public void response200HeaderTest() {
        //given
        int contentSize = 50;

        //when
        String response = HttpResponseUtils.response200Header(contentSize);

        //then
        Assert.assertTrue(response.contains("Content-Length: " + contentSize + "\r\n"));
    }
}
