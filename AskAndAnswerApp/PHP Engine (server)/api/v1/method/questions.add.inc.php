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

    $questionText = isset($_POST['questionText']) ? $_POST['questionText'] : '';
    $questionType = isset($_POST['questionType']) ? $_POST['questionType'] : QUESTION_TYPE_DEFAULT;

    $toUserId = isset($_POST['toUserId']) ? $_POST['toUserId'] : 0;
    $fromUserId = isset($_POST['fromUserId']) ? $_POST['fromUserId'] : 0;

    $toUserId = helper::clearInt($toUserId);
    $fromUserId = helper::clearInt($fromUserId);

    $questionText = str_replace(array("\r", "\n"), " ", $questionText);
    $questionText  = preg_replace('/\s+/', ' ', $questionText );

    $questionText = helper::clearText($questionText);
    $questionText = helper::escapeText($questionText);

    $questionType = helper::clearInt($questionType);

    $result = array("error" => true);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        $accountId = 0;
        $fromUserId = 0;
    }

    unset($auth);

    if ($fromUserId != 0 && $accountId != 0) {

        $fromUserId = $accountId;
    }

    $profile = new profile($dbo, $toUserId);
    $profile->setRequestFrom($accountId);

    $profileData = array();
    $profileData = $profile->get($toUserId);

    //User not found
    if ($profileData['error'] == true) {

        api::printError(ERROR_UNKNOWN, "User not found");
    }

    //User profile disabled or blocked
    if ($profileData['state'] != ACCOUNT_STATE_ENABLED) {

        api::printError(ERROR_UNKNOWN, "User profile disabled or blocked");
    }

    //account id exists in the blacklist
    if ($accountId != 0 && $profileData['blocked'] === true) {

        api::printError(ERROR_UNKNOWN, "Blocked by user");
    }

    //This user does not wish to receive anonymous questions
    if ($profileData['anonymousQuestions'] == DISABLE_ANONYMOUS_QUESTIONS && $fromUserId == 0) {

        api::printError(ERROR_UNKNOWN, "This user does not wish to receive anonymous questions");
    }

    $questions = new questions($dbo);
    $questions->setRequestFrom($accountId);
    $result = $questions->add($questionText, $toUserId, $fromUserId, $questionType);

    echo json_encode($result);
    exit;
}
