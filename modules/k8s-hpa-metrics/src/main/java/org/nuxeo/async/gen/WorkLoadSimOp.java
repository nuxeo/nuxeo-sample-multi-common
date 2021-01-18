package org.nuxeo.async.gen;

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