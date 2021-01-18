import org.junit.Test;
import org.nuxeo.login.LoginTenantHelper;

public class TestColors {

	@Test
	public void testColors() {
		
		LoginTenantHelper lth = new LoginTenantHelper();
		
		System.out.println(lth.getColor4Tenant("tenant1"));
		System.out.println(lth.getColor4Tenant("tenant2"));
		System.out.println(lth.getColor4Tenant("tenant3"));
	}
}
