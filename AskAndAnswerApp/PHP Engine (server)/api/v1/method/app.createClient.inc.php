<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

if (!empty($_GET)) {

    $client_name = isset($_GET['client_name']) ? $_GET['client_name'] : '';

    $client_name = helper::clearText($client_name);

    $result = array();

    $result = $app->createClient($client_name);

    echo json_encode($result);
}
