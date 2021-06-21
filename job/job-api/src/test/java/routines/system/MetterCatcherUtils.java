/*
 * #%L
 * Talend :: ESB :: Job :: API
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
// ============================================================================
//
// Talend Community Edition
//
// Copyright (c) 2006-2021 Talend Inc. - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package routines.system;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MetterCatcherUtils {

    public class MetterCatcherMessage {

        private String origin;

        private Date moment;

        private String jobVersion;

        private String jobId;

        private Long systemPid;

        private String label;

        private Integer count;

        private String referense;

        private String thresholds;

        public MetterCatcherMessage(String label, Integer count, String referense, String thresholds, String origin,
                String jobVersion, String jobId) {
            this.moment = java.util.Calendar.getInstance().getTime();
            this.jobVersion = jobVersion;
            this.jobId = jobId;
            this.systemPid = MetterCatcherUtils.getPid();
            this.origin = origin;

            this.label = label;
            this.count = count;
            this.referense = referense;
            this.thresholds = thresholds;
        }

        public Integer getCount() {
            return this.count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getLabel() {
            return this.label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getReferense() {
            return this.referense;
        }

        public void setReferense(String referense) {
            this.referense = referense;
        }

        public String getThresholds() {
            return this.thresholds;
        }

        public void setThresholds(String thresholds) {
            this.thresholds = thresholds;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public Date getMoment() {
            return moment;
        }

        public void setMoment(Date d) {
            this.moment = d;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getJobVersion() {
            return jobVersion;
        }

        public void setJobVersion(String jobVersion) {
            this.jobVersion = jobVersion;
        }

        public Long getSystemPid() {
            return systemPid;
        }

        public void setSystemPid(Long systemPid) {
            this.systemPid = systemPid;
        }
    }

    ThreadLocal tl_messages = new ThreadLocal();

    private java.util.List<MetterCatcherMessage> getTLMessages() {
        Object value = tl_messages.get();
        if (value == null) {
            java.util.List<MetterCatcherMessage> messages =

            java.util.Collections.synchronizedList(new

            java.util.ArrayList<MetterCatcherMessage>());
            tl_messages.set(messages);
            return messages;
        }

        return (java.util.List<MetterCatcherMessage>) value;
    }

    String jobId = ""; //$NON-NLS-1$

    String jobVersion = ""; //$NON-NLS-1$

    public MetterCatcherUtils(String jobId, String jobVersion) {
        this.jobId = jobId;
        this.jobVersion = jobVersion;
    }

    public void addMessage(String label, Integer count, String referense, String thresholds, String origin) {
        java.util.List<MetterCatcherMessage> messages = getTLMessages();

        MetterCatcherMessage scm = new MetterCatcherMessage(label, count, referense, thresholds, origin, this.jobVersion,
                this.jobId);
        messages.add(scm);
    }

    public java.util.List<MetterCatcherMessage> getMessages() {
        java.util.List<MetterCatcherMessage> messages = getTLMessages();

        java.util.List<MetterCatcherMessage> messagesToSend = new java.util.ArrayList<MetterCatcherMessage>();
        synchronized (messages) {
            for (MetterCatcherMessage scm : messages) {
                messagesToSend.add(scm);
            }
        }
        messages.clear();
        return messagesToSend;
    }

    private Map<String, Integer> connCountMap = java.util.Collections.synchronizedMap(new HashMap<String, Integer>());

    public void clearConnCountMap() {
        connCountMap.clear();
    }

    public void addLineToRow(String connName) {
        if (connCountMap.containsKey(connName)) {
            Integer count = this.connCountMap.get(connName);
            this.connCountMap.put(connName, new Integer(count.intValue() + 1));
        } else {
            this.connCountMap.put(connName, new Integer(1));
        }
    }

    public Integer getConnLinesCount(String connName) {
        return this.connCountMap.get(connName);
    }

    public static long getPid() {
        RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        String[] mxNameTable = mx.getName().split("@"); //$NON-NLS-1$
        if (mxNameTable.length == 2) {
            return Long.parseLong(mxNameTable[0]);
        } else {
            return Thread.currentThread().getId();
        }
    }
}
