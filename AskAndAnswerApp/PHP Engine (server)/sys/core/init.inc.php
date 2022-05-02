<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk, qascript@mail.ru
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

include_once("../sys/config/db.inc.php");
include_once("../sys/config/lang.inc.php");

foreach ($C as $name => $val) {

    define($name, $val);
}

foreach ($B as $name => $val) {

    define($name, $val);
}

$dsn = "mysql:host=".DB_HOST.";dbname=".DB_NAME;
$dbo = new PDO($dsn, DB_USER, DB_PASS, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));

function __autoload($class)
{
    $filename = "../sys/class/class.".$class.".inc.php";

    if (file_exists($filename)) {

        include_once($filename);
    }
}

ini_set('session.cookie_domain', '.'.APP_HOST);
session_set_cookie_params(0, '/', '.'.APP_HOST);

$helper = new helper($dbo);
$auth = new auth($dbo);
