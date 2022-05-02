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

    $questionId = isset($_POST['questionId']) ? $_POST['questionId'] : 0;
    $answerText = isset($_POST['answerText']) ? $_POST['answerText'] : 0;
    $answerImg = isset($_POST['answerImg']) ? $_POST['answerImg'] : '';

    $questionId = helper::clearInt($questionId);

    $answerText = helper::clearText($answerText);

    $answerText = preg_replace( "/[\r\n]+/", "<br>", $answerText );
    $answerText = preg_replace('/\s+/', ' ', $answerText );

    $answerText = helper::escapeText($answerText);

    $result = array("error" => true);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        api::printError(ERROR_ACCESS_TOKEN, "Error authorization.");
    }


    $questions = new questions($dbo);
    $questions->setRequestFrom($accountId);
    $result = $questions->reply($questionId, $answerText, $answerImg);

    echo json_encode($result);
    exit;
}
