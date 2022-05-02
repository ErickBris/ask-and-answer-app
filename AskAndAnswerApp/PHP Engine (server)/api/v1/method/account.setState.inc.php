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

    $accountId = isset($_POST['accountId']) ? $_POST['accountId'] : '';
    $accessToken = isset($_POST['accessToken']) ? $_POST['accessToken'] : '';

    $accountState = isset($_POST['accountState']) ? $_POST['accountState'] : '';

    $fullname = helper::clearText($fullname);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        api::printError(ERROR_ACCESS_TOKEN, "Error authorization.");
    }

    $result = array("error" => false,
                    "error_code" => ERROR_SUCCESS);

    $account = new account($dbo, $accountId);

    $currentState = $account->getState();

    if ($currentState == ACCOUNT_STATE_DEACTIVATED) {

        $questions = new questions($dbo);
        $questions->setRequestFrom($accountId);

        $questions->random($account->getLanguage());
        $questions->random($account->getLanguage());
        $questions->random($account->getLanguage());
        $questions->random($account->getLanguage());
        $questions->random($account->getLanguage());

        unset($questions);
    }

    if ($currentState != ACCOUNT_STATE_BLOCKED) {

        $account->setState($accountState);
    }

    echo json_encode($result);
    exit;
}
