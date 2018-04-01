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
}
