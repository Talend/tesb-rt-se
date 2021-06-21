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
// Copyright (c) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
//
// ============================================================================
package routines.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RunTrace implements Runnable {

    private boolean openSocket = true;

    public void openSocket(boolean openSocket) {
        this.openSocket = openSocket;
    }

    private class TraceBean {

        private String componentId;

        private int nbLine;

        public TraceBean(String componentId) {
            this.componentId = componentId;
        }

        public String getComponentId() {
            return this.componentId;
        }

        public void setComponentId(String componentId) {
            this.componentId = componentId;
        }

        public int getNbLine() {
            return this.nbLine;
        }

        public void setNbLine(int nbLine) {
            this.nbLine = nbLine;
        }
    }

    private java.util.concurrent.ConcurrentHashMap<String, TraceBean> processTraces = new java.util.concurrent.ConcurrentHashMap<String, TraceBean>();

    private Map<String, String> subjobMap = new HashMap<String, String>();

    private java.net.Socket s;

    private java.io.PrintWriter pred;

    private boolean jobIsFinished = false;

    private String str = ""; //$NON-NLS-1$

    private Thread t;

    public void startThreadTrace(String clientHost, int portTraces) throws java.io.IOException, java.net.UnknownHostException {
        if (!openSocket) {
            return;
        }
        System.out.println("[trace] connecting to socket on port " + portTraces); //$NON-NLS-1$
        s = new java.net.Socket(clientHost, portTraces);
        pred = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(s.getOutputStream())), true);
        System.out.println("[trace] connected"); //$NON-NLS-1$
        t = new Thread(this);
        t.start();

    }

    public void run() {
        synchronized (this) {
            try {
                while (!jobIsFinished) {
                    wait(100);
                }
            } catch (InterruptedException e) {
                System.out.println("[trace] interrupted"); //$NON-NLS-1$
            }
        }
    }

    public void stopThreadTrace() {
        if (!openSocket) {
            return;
        }
        jobIsFinished = true;
        try {
            pred.close();
            s.close();
            System.out.println("[trace] disconnected"); //$NON-NLS-1$
        } catch (java.io.IOException ie) {
        }
    }

    public boolean isNextRow() {
        if (!openSocket) {
            return false;
        }
        InputStream in;
        try {
            askForStatus();
            in = s.getInputStream();
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            return "NEXT_ROW".equals(reader.readLine()); //$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isNextBreakpoint() {
        if (!openSocket) {
            return false;
        }
        InputStream in;
        try {
            askForStatus();
            in = s.getInputStream();
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            // setNextRow();
            return "NEXT_BREAKPOINT".equals(reader.readLine()); //$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void waitForUserAction() throws InterruptedException {
        if (!openSocket) {
            return;
        }
        InputStream in;
        try {
            boolean action = false;
            pred.println("UI_STATUS"); //$NON-NLS-1$
            do {
                in = s.getInputStream();
                LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
                String line = reader.readLine();
                if ("STATUS_WAITING".equals(line)) {
                    pred.println("UI_STATUS");
                    Thread.sleep(100);
                } else {
                    action = true;
                }
            } while (!action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPause() {
        if (!openSocket) {
            return false;
        }
        InputStream in;
        try {
            askForStatus();
            in = s.getInputStream();
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            return "PAUSE".equals(reader.readLine()); //$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void askForStatus() {
        pred.println("ID_STATUS"); //$NON-NLS-1$
    }

    private String componentName = "";

    public void sendTrace(String componentId, String startNodeCid, String datas) {
        if (!openSocket) {
            return;
        }
        subjobMap.put(componentId, startNodeCid);
        Iterator<Entry<String, String>> ite = subjobMap.entrySet().iterator();
        boolean sameSub = false;
        while (ite.hasNext()) {
            Entry<String, String> en = ite.next();
            if (en.getKey().equals(componentId)) {
                continue;
            }
            if (en.getValue().equals(startNodeCid)) {
                sameSub = true;
                break;
            }
        }
        if (sameSub && processTraces.size() > 1) { // if the connections are more than one, will check
            if (componentId.equals(componentName)) {
                return;
            }
        }
        TraceBean bean;
        if (processTraces.containsKey(componentId)) {
            bean = processTraces.get(componentId);
        } else {
            bean = new TraceBean(componentId);
        }
        bean.setNbLine(bean.getNbLine() + 1);
        processTraces.put(componentId, bean);

        str = bean.getComponentId() + "|" + bean.getNbLine() + "|" + datas; //$NON-NLS-1$ //$NON-NLS-2$
        pred.println(str); // envoi d'un message
        componentName = componentId;
    }
}
