/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.eygsl.cbs.referencemsal.utils;

public class Constants {
    public static final String SUPPORT_EMAIL= "mobile.app.services@ey.com";
    public static final String[] MSAL_SCOPES = {"https://graph.microsoft.com/User.Read"};
    public static final String[] INTERNAL_WEB_LINK_SCOPE = {"https://americasreservemobile.ey.net//user_impersonation"};
    public static final String MS_GRAPH_ROOT = "https://graph.microsoft.com/";
    public static final String MS_GRAPH_ENDPOINT = "v1.0/me";
    // Personal
    // public static final String APPCENTER_APP_SECRET= "d16b2bad-57c6-44b1-a8c0-94dcbb8bf153";
    // Org
    // public static final String APPCENTER_APP_SECRET= "b8cbe1a3-c4be-402d-8d78-7d814b010682";
    // Org MSP account secret key
    public static final String APPCENTER_APP_SECRET= "b5d0cd99-e115-4fe1-8908-a0a014a22c83";
    public static final String SHAREPOINT_LIST_URL = "https://graph.microsoft.com/v1.0/sites/sites.ey.com:/sites/MAS:/lists/OnboardingRequest?expand=items(expand=fields)";
    public static final String SHAREPOINT_DOCUMENTS_URL = "https://graph.microsoft.com/v1.0/sites/sites.ey.com,31b1e6b6-981c-4bb5-a5c9-1ae1d3520f15,27a6d864-724f-486e-bb42-099c67f010b5/lists/documents/drive/root/children";
    public static final String FAQ_LINK = "https://sites.ey.com/sites/mam/SitePages/Mobile%20Services.aspx/";
    public static final String INTERNAL_WEB_LINK = "https://gbbplus.ey.net/GBBClient/";
    public static final String EXTERNAL_WEB_LINK = "https://sites.ey.com/";
    public static final String VIDEO_LINK = "https://web.microsoftstream.com/video/6d14a791-87c2-418e-b748-840f92a30e5c?list=trending";
    public static final String MS_STREAM_APP_PKG_ID = "com.microsoft.stream";
    public static final String MS_OUTLOOK_APP_PKG_ID = "com.microsoft.office.outlook";
    public static final String MS_EDGE_APP_PKG_ID = "com.microsoft.emmx";
    public static final String MS_STREAM_APP_PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.microsoft.stream";
}
