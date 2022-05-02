<?php

/*!
 * Secret v1.0
 *
 * http://ifsoft.co.uk
 * vsysteme@mail.ru
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

if (!empty($_POST)) {

    $accountId = isset($_POST['accountId']) ? $_POST['accountId'] : 0;
    $accessToken = isset($_POST['accessToken']) ? $_POST['accessToken'] : '';

    $language = isset($_POST['language']) ? $_POST['language'] : 'en';
    $hashtag = isset($_POST['hashtag']) ? $_POST['hashtag'] : '';
    $answerId = isset($_POST['answerId']) ? $_POST['answerId'] : 0;

    $language = helper::clearText($language);
    $language = helper::escapeText($language);

    $hashtag = helper::clearText($hashtag);
    $hashtag = helper::escapeText($hashtag);

    $answerId = helper::clearInt($answerId);

    $result = array("error" => true,
                    "error_code" => ERROR_UNKNOWN);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        api::printError(ERROR_ACCESS_TOKEN, "Error authorization.");
    }

    $hashtags = new hashtag($dbo);
    $hashtags->setRequestFrom($accountId);
//    $hashtags->setLanguage($LANG['lang-code']);

    $result = $hashtags->search($hashtag, $answerId);

    echo json_encode($result);
    exit;
}
