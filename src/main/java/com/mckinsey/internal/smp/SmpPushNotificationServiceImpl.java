package com.mckinsey.internal.smp;

import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javapns.Push;

import javapns.communication.exceptions.CommunicationException;


@Component
@ComponentScan(basePackages = "com.mckinsey.internal.smp")
public class SmpPushNotificationServiceImpl implements SmpPushNotificationService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public String parseNotificationInfo(String resource) {
        JSONObject obj = new JSONObject(resource);
        String messageId = obj.getString("msgId");
        JSONArray jsonArray = obj.getJSONArray("tokens");

        System.out.println(messageId);
        System.out.println(jsonArray.get(0));

        List<String> tokenList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tokenList.add( jsonArray.getString(i) );
        }

        PushNotificationPayload payload = PushNotificationPayload.complex();
        payload.addCustomAlertBody(messageId);
        return this.sendNotifications(payload, tokenList);
    }

    @Override
    public String sendNotifications(PushNotificationPayload payload, List<String> deviceTokens) {
        BasicConfigurator.configure();
        try {
            int count = 0;
            long startTime = System.nanoTime();
            InputStream keystoreInputStream = this.getClass().getClassLoader().getResourceAsStream(Constants.CERTIFICATE_FILE_PATH);
            String certificatePassword = System.getProperty(Constants.CERTIFICATE_PASSWORD_PARAM_NAME);


            List<PushedNotification> notifications = Push.payload(payload, keystoreInputStream, certificatePassword, true, deviceTokens);
            for (PushedNotification notification : notifications) {
                if (notification.isSuccessful()) {
                    /* Notification accepted by APNS */
                    logger.info("Push notification sent successfully for token: " + notification.getDevice().getToken());
                } else {
                    count++;
                    String tokenId = notification.getDevice().getToken();
                    logger.error("Notification failed for token " + tokenId + " with exception: " + notification.getException());

                    // Check if problem is response packet returned by APNS.
                    ResponsePacket response = notification.getResponse();
                    if (response != null) {
                        logger.error(response.getMessage());
                    }
                }
            }
            long endTime = System.nanoTime();
            logger.info("Elapsed time in pushing notifications to APNS: " + (endTime - startTime) / 1000000000 + " second(s)");
            logger.info("Total number of tokens: " + deviceTokens.size());
            logger.info("Notification failed for total tokens: " + count);
            return "OK";
        } catch (CommunicationException e) {
            String errorMessage = "Error in communication with APNS.";
            logger.error(errorMessage, e);
            return errorMessage;
        } catch (KeystoreException e) {
            String errorMessage = "Error in reading keystore file.";
            logger.error( errorMessage, e);
            return  errorMessage;
        }
    }
}
