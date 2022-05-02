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

    $clientId = isset($_POST['clientId']) ? $_POST['clientId'] : 0;

    $username = isset($_POST['username']) ? $_POST['username'] : '';
    $password = isset($_POST['password']) ? $_POST['password'] : '';

    $gcm_regId = isset($_POST['gcm_regId']) ? $_POST['gcm_regId'] : '';

    $clientId = helper::clearInt($clientId);

    $gcm_regId = helper::clearText($gcm_regId);
    $username = helper::clearText($username);
    $password = helper::clearText($password);

    $access_data = array();

    $account = new account($dbo);
    $access_data = $account->signin($username, $password);

    unset($account);

    if ($access_data["error"] === false) {

        $auth = new auth($dbo);
        $access_data = $auth->create($access_data['accountId'], $clientId);

        if ($access_data['error'] === false) {

            $account = new account($dbo, $access_data['accountId']);
            $access_data['account'] = array();

            array_push($access_data['account'], $account->get());

            if (strlen($gcm_regId) != 0) {

                $account->setGCM_regId($gcm_regId);
            }
        }
    }

    echo json_encode($access_data);
    exit;
}
