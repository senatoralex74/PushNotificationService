package com.mckinsey.internal.smp;

import javapns.notification.PushNotificationPayload;

import java.util.List;

public interface SmpPushNotificationService {

    String parseNotificationInfo(String payload);

    String sendNotifications(PushNotificationPayload payload, List<String> deviceTokens);

}
