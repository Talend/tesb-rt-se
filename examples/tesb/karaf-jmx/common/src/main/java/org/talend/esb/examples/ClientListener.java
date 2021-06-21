/*
 * #%L
 * TESB :: Examples :: Karaf-jmx
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
package org.talend.esb.examples;

import javax.management.Notification;
import javax.management.NotificationListener;

public class ClientListener implements NotificationListener {

    public void handleNotification(Notification notification, Object handback) {
        echo("\nReceived notification:");
        echo("\tClassName: " + notification.getClass().getName());
        echo("\tSource: " + notification.getSource());
        echo("\tType: " + notification.getType());

        String userData = notification.getUserData().toString();
        String contentsMarker = "contents={";
        int eventInfoPosition = userData.indexOf(contentsMarker);
        String eventInfo = userData.substring(eventInfoPosition);
        eventInfo = eventInfo.substring(contentsMarker.length(),
                (eventInfo.length() - 2));
        echo("\tEvent info: " + eventInfo);
    }

    private void echo(String msg) {
        System.out.println(msg);
    }
}
