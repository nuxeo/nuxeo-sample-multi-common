import org.junit.Test;
import org.nuxeo.sync.gen.CPUWorkloadSimOp;

public class TestCPU {

	@Test
	public void test() throws Exception {
		int loops=CPUWorkloadSimOp.doSomething(5);
		System.out.println(loops);
	}
}
