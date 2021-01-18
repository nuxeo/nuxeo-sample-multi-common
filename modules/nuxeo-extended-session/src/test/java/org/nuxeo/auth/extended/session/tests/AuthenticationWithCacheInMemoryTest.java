/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Nuxeo
 */
package org.nuxeo.auth.extended.session.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.auth.extended.session.SessionConstants;
import org.nuxeo.auth.extended.session.SessionIdFilter;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.ServletContainer;

@RunWith(FeaturesRunner.class)
@Features({ HttpServerContainerWithAuthFeature.class })
@ServletContainer(port = 18090)
public class AuthenticationWithCacheInMemoryTest {

	private static final String BASE_URL = "http://localhost:18090";

	private static final String LOGIN = "Administrator";

	private static final String PASSWORD = "Administrator";

	private static final String HEADER_COOKIE_ID = "Set-Cookie";

	protected boolean hasCookie(CloseableHttpResponse response, String cookieName) {
		Header[] headers = response.getHeaders(HEADER_COOKIE_ID);
		for (Header header : headers) {
			if (header.getValue().contains(cookieName)) {
				System.out.println(header.getValue());
				return true;
			}
		}
		return false;
	}

	@Test
	public void testClientWithNoExtendedSession() throws Exception {

		// when secure cookie is on, since we are using http in this test
		// the extended session is technically disabled
		SessionIdFilter.useSecureCookie=true;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String content = doLogin(httpclient);
		System.out.println(content);
		
		content = checkAuthenticated(httpclient);
		System.out.println(content);
		
		killSession(httpclient);
		
		checkNotAuthenticated(httpclient);

	}

	@Test
	public void testClientWithExtendedSession() throws Exception {

		// when secure cookie is on, since we are using http in this test
		// the extended session is technically disabled
		SessionIdFilter.useSecureCookie=false;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String content = doLogin(httpclient);
		System.out.println(content);
		
		content = checkAuthenticated(httpclient);
		System.out.println(content);
		
		killSession(httpclient);
		
		content = checkAuthenticated(httpclient);
		System.out.println(content);
		
		
	}


	protected void killSession(CloseableHttpClient httpclient) throws Exception {
		HttpGet httpGet = new HttpGet(BASE_URL + "/killSession");
		CloseableHttpResponse response = httpclient.execute(httpGet);
		return;
	}
	
	protected String checkAuthenticated(CloseableHttpClient httpclient) throws Exception {
		HttpGet httpGet = new HttpGet(BASE_URL + "/sayHello");

		CloseableHttpResponse response = httpclient.execute(httpGet);
		String content = IOUtils.toString(response.getEntity().getContent(), Consts.UTF_8);
		assertTrue(content.contains("Administrator"));
		
		return content;
	}
	
	protected String checkNotAuthenticated(CloseableHttpClient httpclient) throws Exception {
		HttpGet httpGet = new HttpGet(BASE_URL + "/sayHello");

		CloseableHttpResponse response = httpclient.execute(httpGet);
		String content = IOUtils.toString(response.getEntity().getContent(), Consts.UTF_8);
		
		System.out.println(content);
		assertFalse(content.contains("Administrator"));
		
		return content;

	}


	protected String doLogin(CloseableHttpClient httpclient) throws Exception {

		List<NameValuePair> form = new ArrayList<>();
		form.add(new BasicNameValuePair("user_name", "Administrator"));
		form.add(new BasicNameValuePair("user_password", "Administrator"));
		form.add(new BasicNameValuePair("form_submitted_marker", "yoohoo"));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
		HttpPost httpPost = new HttpPost(BASE_URL + "/login");
		httpPost.setEntity(entity);

		CloseableHttpResponse response = httpclient.execute(httpPost);
		String content = IOUtils.toString(response.getEntity().getContent(), Consts.UTF_8);
		assertTrue(content.contains("Administrator"));

		assertTrue(hasCookie(response, "JSESSIONID"));
		assertTrue(hasCookie(response, SessionConstants.EX_SESSION_COOKIE_NAME));

		return content;
	}

}