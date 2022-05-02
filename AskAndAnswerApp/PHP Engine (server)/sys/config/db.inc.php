<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk, qascript@mail.ru
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

$C = array();
$B = array();

$B['APP_DEMO'] = false;                                     //true = enable demo version mode

$B['APP_PATH'] = "app";
$B['APP_VERSION'] = "2";
$B['APP_NAME'] = "qascript";
$B['APP_TITLE'] = "QA Script";
$B['APP_VENDOR'] = "qascript.com.ua";
$B['APP_YEAR'] = "2015";
$B['APP_AUTHOR'] = "Demyanchuk Dmitry";
$B['APP_SUPPORT_EMAIL'] = "qascript@mail.ru";
$B['APP_AUTHOR_PAGE'] = "qascript";
$B['PHOTO_PATH'] = "photo/";                                //don`t edit this option
$B['ANSWER_PHOTO_PATH'] = "answer/";                        //don`t edit this option
$B['COVER_PATH'] = "cover/";                                //don`t edit this option
$B['TEMP_PATH'] = "tmp/";                                   //don`t edit this option

$B['CLIENT_ID'] = 1;                                        //Android App Client ID

$B['GOOGLE_API_KEY'] = "api key";                    //Server API Key (for sending gcm messages)

$B['APP_HOST'] = "qascript.com.ua";                         //edit to your domain example: http://yourdomain.com
$B['APP_URL'] = "http://qascript.com.ua";                   //edit to your domain url
$B['GOOGLE_PLAY_LINK'] = "https://play.google.com/store/apps/details?id=ua.com.qascript.android";

$B['SMTP_EMAIL'] = 'support@qascript.com.ua';               //SMTP email
$B['SMTP_USERNAME'] = 'support@qascript.com.ua';            //SMTP username
$B['SMTP_PASSWORD'] = 'smtp password';                      //SMTP password

//Please edit database data

$C['DB_HOST'] = "localhost";                                //localhost or your db host
$C['DB_USER'] = "root";                                     //your db user
$C['DB_PASS'] = "";                                         //your db password
$C['DB_NAME'] = "you database name";                        //your db name


$C['COMPANY_URL'] = "http://qascript.com.ua";

$C['GCM_NOTIFY_CONFIG'] = 0;
$C['GCM_NOTIFY_SYSTEM'] = 1;
$C['GCM_NOTIFY_CUSTOM'] = 2;
$C['GCM_NOTIFY_LIKE'] = 3;
$C['GCM_NOTIFY_ANSWER'] = 4;
$C['GCM_NOTIFY_QUESTION'] = 5;
$C['GCM_NOTIFY_COMMENT'] = 6;
$C['GCM_NOTIFY_FOLLOWER'] = 7;

$C['ERROR_SUCCESS'] = 0;

$C['ERROR_UNKNOWN'] = 100;
$C['ERROR_ACCESS_TOKEN'] = 101;

$C['ERROR_LOGIN_TAKEN'] = 300;
$C['ERROR_EMAIL_TAKEN'] = 301;

$C['ERROR_ACCOUNT_ID'] = 400;

$C['QUESTION_TYPE_DEFAULT'] = 0;

$C['DISABLE_ANONYMOUS_QUESTIONS'] = 0;
$C['ENABLE_ANONYMOUS_QUESTIONS'] = 1;

$C['USER_CREATED_SUCCESSFULLY'] = 0;
$C['USER_CREATE_FAILED'] = 1;
$C['USER_ALREADY_EXISTED'] = 2;
$C['USER_BLOCKED'] = 3;
$C['USER_NOT_FOUND'] = 4;
$C['USER_LOGIN_SUCCESSFULLY'] = 5;
$C['EMPTY_DATA'] = 6;
$C['ERROR_API_KEY'] = 7;

$C['NOTIFY_TYPE_LIKE'] = 0;
$C['NOTIFY_TYPE_ANSWER'] = 1;

$C['QUESTION_TYPE_DEFAULT'] = 0;
$C['QUESTION_TYPE_RANDOM'] = 1;

$C['ACCOUNT_STATE_ENABLED'] = 0;
$C['ACCOUNT_STATE_DISABLED'] = 1;
$C['ACCOUNT_STATE_BLOCKED'] = 2;
$C['ACCOUNT_STATE_DEACTIVATED'] = 3;

$LANGS = array();
$LANGS['English'] = "en";

