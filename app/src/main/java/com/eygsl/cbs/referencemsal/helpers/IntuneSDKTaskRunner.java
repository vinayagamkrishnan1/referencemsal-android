/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.eygsl.cbs.referencemsal.helpers;

import android.app.Application;
import android.util.Log;

import com.eygsl.cbs.referencemsal.authentication.AuthenticationCallback;
import com.eygsl.cbs.referencemsal.room.RoomManager;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiverRegistry;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import com.microsoft.intune.mam.policy.notification.MAMEnrollmentNotification;
import com.microsoft.intune.mam.policy.notification.MAMNotificationType;

/**
 * Specifies what happens when the app is launched and terminated.
 *
 * Registers an authentication callback for MAM.
 */
public class IntuneSDKTaskRunner extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the tasks database
        RoomManager.initRoom(getApplicationContext());

        // Registers a MAMAuthenticationCallback, which will try to acquire access tokens for MAM.
        // This is necessary for proper MAM integration.
        MAMEnrollmentManager mgr = MAMComponents.get(MAMEnrollmentManager.class);
        mgr.registerAuthenticationCallback(new AuthenticationCallback(getApplicationContext()));

        /* This section shows how to register a MAMNotificationReceiver, so you can perform custom
         * actions based on MAM enrollment notifications.
         * More information is available here:
         * https://docs.microsoft.com/en-us/intune/app-sdk-android#types-of-notifications */
        MAMComponents.get(MAMNotificationReceiverRegistry.class).registerReceiver(notification -> {
            if (notification instanceof MAMEnrollmentNotification) {
                MAMEnrollmentManager.Result result =
                        ((MAMEnrollmentNotification) notification).getEnrollmentResult();
                switch (result) {
                    case AUTHORIZATION_NEEDED:
                    case NOT_LICENSED:
                    case ENROLLMENT_SUCCEEDED:
                    case ENROLLMENT_FAILED:
                    case WRONG_USER:
                    case UNENROLLMENT_SUCCEEDED:
                    case UNENROLLMENT_FAILED:
                    case PENDING:
                    case COMPANY_PORTAL_REQUIRED:
                    default:
                        Log.d("Enrollment Receiver", result.name());
                        break;
                }
            } else {
                Log.d("Enrollment Receiver", "Unexpected notification type received");
            }
            return true;
        }, MAMNotificationType.MAM_ENROLLMENT_RESULT);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Close the database connection to prevent memory leaks
        RoomManager.closeRoom();
    }
}
