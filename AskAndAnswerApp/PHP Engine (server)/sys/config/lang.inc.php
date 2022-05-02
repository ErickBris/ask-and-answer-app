<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    if (isset($_COOKIE['lang'])) {

        $language = $_COOKIE['lang'];

        $result = "en";

        if (in_array($language, $LANGS)) {

            $result = $language;
        }

        @setcookie("lang", $result, time() + 14 * 24 * 3600, "/");
        include_once("../sys/lang/".$result.".php");

    }  else {

        $language = substr($_SERVER['HTTP_ACCEPT_LANGUAGE'], 0, 2);

        $result = "en";

        if (in_array($language, $LANGS)) {

            $result = $language;
        }

        @setcookie("lang", $result, time() + 14 * 24 * 3600, "/");
        include_once("../sys/lang/".$result.".php");
    }

    $LANG = $TEXT;

