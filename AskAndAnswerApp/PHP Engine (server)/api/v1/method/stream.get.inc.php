<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

if (!empty($_POST)) {

    $accountId = isset($_POST['accountId']) ? $_POST['accountId'] : 0;
    $accessToken = isset($_POST['accessToken']) ? $_POST['accessToken'] : '';

    $language = isset($_POST['language']) ? $_POST['language'] : 'en';
    $replyAt = isset($_POST['replyAt']) ? $_POST['replyAt'] : 0;

    $language = helper::clearText($language);
    $language = helper::escapeText($language);

    $replyAt = helper::clearInt($replyAt);

    $result = array("error" => true);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        api::printError(ERROR_ACCESS_TOKEN, "Error authorization.");
    }


    $stream = new stream($dbo);
    $stream->setRequestFrom($accountId);
    $result = $stream->get($replyAt, $language);

    echo json_encode($result);
    exit;
}
