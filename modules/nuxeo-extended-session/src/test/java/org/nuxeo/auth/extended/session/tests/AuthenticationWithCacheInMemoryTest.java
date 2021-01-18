/*
 * (C) Copyright 2021 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Thierry Delprat
 */
package org.nuxeo.auth.extended.session.tests;

import static org.junit.Assert.assertEquals;
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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger log = LogManager.getLogger(AuthenticationWithCacheInMemoryTest.class);

    private static final String BASE_URL = "http://localhost:18090";

    private static final String LOGIN = "Administrator";

    private static final String PASSWORD = "Administrator";

    private static final String HELLO_CONTENT = String.format(DummyServlet.HELLO_PATTERN, LOGIN);

    private static final String HEADER_COOKIE_ID = "Set-Cookie";

    protected boolean hasCookie(CloseableHttpResponse response, String cookieName) {
        Header[] headers = response.getHeaders(HEADER_COOKIE_ID);
        for (Header header : headers) {
            if (header.getValue().contains(cookieName)) {
                log.debug(header.getValue());
                return true;
            }
        }
        return false;
    }

    @Test
    public void testClientWithNoExtendedSession() throws Exception {

        // when secure cookie is on, since we are using http in this test
        // the extended session is technically disabled
        SessionIdFilter.useSecureCookie = true;

        CloseableHttpClient httpclient = HttpClients.createDefault();

        doLogin(httpclient);
        checkAuthenticated(httpclient);

        killSession(httpclient);

        checkNotAuthenticated(httpclient);
    }

    @Test
    public void testClientWithExtendedSession() throws Exception {

        // when secure cookie is on, since we are using http in this test
        // the extended session is technically disabled
        SessionIdFilter.useSecureCookie = false;

        CloseableHttpClient httpclient = HttpClients.createDefault();

        doLogin(httpclient);
        checkAuthenticated(httpclient);

        killSession(httpclient);

        checkAuthenticated(httpclient);
    }

    protected String execute(CloseableHttpClient httpclient, HttpUriRequest request) throws Exception {
        CloseableHttpResponse response = httpclient.execute(request);
        return IOUtils.toString(response.getEntity().getContent(), Consts.UTF_8);
    }

    protected void killSession(CloseableHttpClient httpclient) throws Exception {
        HttpGet httpGet = new HttpGet(BASE_URL + DummyServlet.KILL_URI);
        execute(httpclient, httpGet);
    }

    protected void checkAuthenticated(CloseableHttpClient httpclient) throws Exception {
        HttpGet httpGet = new HttpGet(BASE_URL + DummyServlet.HELLO_URI);
        assertEquals(HELLO_CONTENT, execute(httpclient, httpGet));
    }

    protected void checkNotAuthenticated(CloseableHttpClient httpclient) throws Exception {
        HttpGet httpGet = new HttpGet(BASE_URL + DummyServlet.HELLO_URI);
        String content = execute(httpclient, httpGet);
        assertFalse(content.contains(LOGIN));
    }

    protected void doLogin(CloseableHttpClient httpclient) throws Exception {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("user_name", LOGIN));
        form.add(new BasicNameValuePair("user_password", PASSWORD));
        form.add(new BasicNameValuePair("form_submitted_marker", "yoohoo"));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        HttpPost httpPost = new HttpPost(BASE_URL + DummyServlet.LOGIN_URI);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httpPost);
        String content = IOUtils.toString(response.getEntity().getContent(), Consts.UTF_8);
        assertEquals(HELLO_CONTENT, content);

        assertTrue(hasCookie(response, "JSESSIONID"));
        assertTrue(hasCookie(response, SessionConstants.EX_SESSION_COOKIE_NAME));
    }

}