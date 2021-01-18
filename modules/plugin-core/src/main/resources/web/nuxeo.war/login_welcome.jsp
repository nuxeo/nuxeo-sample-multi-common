<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@ page import="org.joda.time.DateTime"%>
<%@ page import="org.nuxeo.ecm.core.api.repository.RepositoryManager"%>
<%@ page import="org.nuxeo.ecm.platform.ui.web.auth.LoginScreenHelper"%>
<%@ page import="org.nuxeo.ecm.platform.web.common.MobileBannerHelper"%>
<%@ page import="org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants"%>
<%@ page import="org.nuxeo.ecm.platform.ui.web.auth.service.LoginProviderLink"%>
<%@ page import="org.nuxeo.ecm.platform.ui.web.auth.service.LoginScreenConfig"%>
<%@ page import="org.nuxeo.ecm.platform.web.common.admin.AdminStatusHelper"%>
<%@ page import="org.nuxeo.common.Environment"%>
<%@ page import="org.nuxeo.runtime.api.Framework"%>
<%@ page import="org.nuxeo.login.LoginTenantHelper"%>
<%@ page import="org.nuxeo.ecm.platform.ui.web.auth.service.LoginVideo" %>
<%@ page import="org.nuxeo.ecm.platform.web.common.locale.LocaleProvider"%>

<%
String tenantId = Framework.getProperty("nuxeo.tenantId", "Unknown");
%>
<div class="welcome">
  <p class="welcomeText">
    Welcome to Tenant <%=tenantId%>.
  </p>
</div>

<script>
var text = "<%=tenantId%>";	
var canvas = document.createElement("canvas");
var fontSize = 32;
canvas.setAttribute('height', 2*fontSize );
var context = canvas.getContext('2d');
context.fillStyle    = 'rgba(0,0,0,0.7)';
context.font         = fontSize + 'px sans-serif';
context.fillText(text, 0, fontSize);
var body = document.getElementsByTagName("body")[0];
body.style.backgroundImage="url(" + canvas.toDataURL("image/png")+ ")"
body.style.backgroundSize="auto"
body.style.backgroundRepeat="repeat"
body.style.backgroundColor="<%=LoginTenantHelper.getColor()%>";
</script>
 