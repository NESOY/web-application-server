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
	public void response200Header_Content_Length_Test() {
		//given
		String mockResourcePath = "Hello.html";
		int contentSize = 50;

		//when
		String response = HttpResponseUtils.response200Header(mockResourcePath, contentSize);

		//then
		Assert.assertTrue(response.contains("Content-Length: " + contentSize + "\r\n"));
	}

	@Test
	public void getContentTypeTest_CSS() {
		//given
		String resourePath = "hello.css";

		//when
		String contentType = HttpResponseUtils.getContentType(resourePath);

		//then
		Assert.assertEquals("Content-Type: text/css\r\n", contentType);
	}

	@Test
	public void getContentTypeTest_FONT_woff2() {
		//given
		String resourePath = "hell.woff2";

		//when
		String contentType = HttpResponseUtils.getContentType(resourePath);

		//then
		Assert.assertEquals("Content-Type: font/woff2\r\n", contentType);
	}

	@Test
	public void getContentTypeTest_javascript() {
		//given
		String resourePath = "hell.js";

		//when
		String contentType = HttpResponseUtils.getContentType(resourePath);

		//then
		Assert.assertEquals("Content-Type: text/javascript\r\n", contentType);
	}
}
