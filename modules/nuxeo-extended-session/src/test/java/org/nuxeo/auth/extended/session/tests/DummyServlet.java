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

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.platform.ui.web.auth.NuxeoSecuredRequestWrapper;

public class DummyServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(DummyServlet.class);

    private static final long serialVersionUID = 1L;

    public static final String LOGIN_URI = "/login";

    public static final String HELLO_URI = "/sayHello";

    public static final String HELLO_PATTERN = "Hello %s";

    public static final String KILL_URI = "/killSession";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("Starting the Dummy Servlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uri = req.getRequestURI();

        if (uri.startsWith(HELLO_URI)) {
            sayHello(req, resp);
        } else if (uri.startsWith(KILL_URI)) {
            invalidateSession(req);
        } else {
            log.info(" requested: {}", uri);
        }

        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uri = req.getRequestURI();

        if (uri.startsWith(LOGIN_URI)) {
            log.info("login");
            sayHello(req, resp);
        } else {
            log.info("POST requested: {}", uri);
        }

        resp.setStatus(200);
        ;
    }

    protected void invalidateSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    protected void sayHello(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req instanceof NuxeoSecuredRequestWrapper) {
            Principal principal = ((NuxeoSecuredRequestWrapper) req).getUserPrincipal();
            resp.getWriter().append(String.format(HELLO_PATTERN, principal.getName()));
        } else {
            resp.getWriter().append("No body");
        }
        resp.flushBuffer();
    }

}
