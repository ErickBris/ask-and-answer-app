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

    $anonymousQuestions = isset($_POST['anonymousQuestions']) ? $_POST['anonymousQuestions'] : 1;

    $anonymousQuestions = helper::clearInt($anonymousQuestions);

    $result = array("error" => true);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        api::printError(ERROR_ACCESS_TOKEN, "Error authorization.");
    }

    $account = new account($dbo, $accountId);
    $result = $account->setAnonymousQuestions($anonymousQuestions);

    if ($result['error'] === false) {

        $result = $account->get();
    }

    echo json_encode($result);
    exit;
}
