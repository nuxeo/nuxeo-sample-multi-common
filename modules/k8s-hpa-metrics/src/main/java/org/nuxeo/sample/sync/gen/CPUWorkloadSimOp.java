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