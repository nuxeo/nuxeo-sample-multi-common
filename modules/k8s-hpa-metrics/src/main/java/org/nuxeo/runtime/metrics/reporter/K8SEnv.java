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
package org.nuxeo.runtime.metrics.reporter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class K8SEnv {

    private static final Logger log = LogManager.getLogger(K8SEnv.class);

    protected String getPodInfoFromEnv(String name) {
        String key = name.toUpperCase();
        if ("NAMESPACE".equals(key)) {
            key = "POD_NAMESPACE";
        }
        return System.getenv().get(key);
    }

    protected String getPodInfoFromFS(String name) {
        String path = "/etc/podinfo/" + name;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Unable to read {}", path, e);
        }
        return null;
    }

    protected String getPodInfo(String attribute) {

        String podInfo = getPodInfoFromEnv(attribute);
        if (podInfo == null) {
            podInfo = getPodInfoFromFS(attribute);
        }
        if (podInfo == null) {
            log.debug("Unable to find podInfo from env or FS: {}", attribute);
        }
        return podInfo;
    }

}
