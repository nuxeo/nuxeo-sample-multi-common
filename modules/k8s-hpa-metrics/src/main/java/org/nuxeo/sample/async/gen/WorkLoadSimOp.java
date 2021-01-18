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
package org.nuxeo.sample.async.gen;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.work.SleepWork;
import org.nuxeo.ecm.core.work.api.WorkManager;

/**
 *
 */
@Operation(id = WorkLoadSimOp.ID, category = Constants.CAT_DOCUMENT, label = "WorkLoadSimOp", description = "Describe here what your operation does.")
public class WorkLoadSimOp {

    public static final String ID = "Workload.Simulate";

    @Context
    protected WorkManager wm;

    @Context
    protected CoreSession session;

    @Param(name = "nbWork", required = false)
    protected Integer nbWork;

    @Param(name = "duration", required = false)
    protected Integer durationS = 10;

    @OperationMethod
    public void run() {

        for (int i = 0; i < nbWork; i++) {
            wm.schedule(new SleepWork(1000 * durationS));
        }

    }
}