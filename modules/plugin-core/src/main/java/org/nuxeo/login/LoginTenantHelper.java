package org.nuxeo.login;

import java.security.MessageDigest;

import org.nuxeo.runtime.api.Framework;

public class LoginTenantHelper {

	public static String getColor() {
		String tenantId = Framework.getProperty("nuxeo.tenantId", "Unknown");
		return getColor4Tenant(tenantId);
	}
		
	public static String getColor4Tenant(String tenantId) {
		
		String token=tenantId;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
   		    md.update(tenantId.getBytes());
			byte[] digest = md.digest();
			token = new String(digest, "UTF-8");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		int color = token.hashCode();
		
		String hexColor = String.format("#%06X", (0xFFFFFF & color));
	
		return hexColor;
	}
	
}
