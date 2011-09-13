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
