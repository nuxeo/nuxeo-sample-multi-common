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
package org.nuxeo.sample.hpa.tests;

import org.nuxeo.ecm.core.work.SleepWork;

public class SleepyWork extends SleepWork {

    private static final long serialVersionUID = 1L;

    public SleepyWork(long durationMillis) {
        super(durationMillis);
    }

    @Override
    protected void doWork() throws InterruptedException {
        System.out.println("Working!!!");
        super.doWork();
    }

}
