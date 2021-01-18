package org.nuxeo.sample.sync.gen;

import java.security.MessageDigest;
import java.util.UUID;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;

@Operation(id = CPUWorkloadSimOp.ID, category = Constants.CAT_DOCUMENT, label = "CPUWorkloadSimOp", description = "Describe here what your operation does.")
public class CPUWorkloadSimOp {

    public static final String ID = "CPUWorkload.Simulate";

    @Param(name = "duration", required = false)
    protected Integer durationS = 10;

    @OperationMethod
    public void run() {
        try {
            doSomething(durationS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int doSomething(long seconds) throws Exception {
        long t0 = System.currentTimeMillis();
        int loops = 0;
        while (System.currentTimeMillis() - t0 < seconds * 1000) {
            doSomething();
            loops++;
        }
        return loops;
    }

    public static void doSomething() throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1000; i++) {
            sb.append(UUID.randomUUID().toString());
            digest.update(sb.toString().getBytes());
        }
    }
}