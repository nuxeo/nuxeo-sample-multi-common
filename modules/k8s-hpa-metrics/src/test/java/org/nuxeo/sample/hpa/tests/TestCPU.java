package org.nuxeo.sample.hpa.tests;
import org.junit.Test;
import org.nuxeo.sample.sync.gen.CPUWorkloadSimOp;

public class TestCPU {

    @Test
    public void test() throws Exception {
        int loops = CPUWorkloadSimOp.doSomething(5);
        System.out.println(loops);
    }
}
