<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $page_id = "update";

    include_once("../sys/core/initialize.inc.php");

    $update = new update($dbo);

    $css_files = array("sprosi.css");
    $page_title = APP_TITLE;

    include_once("../html/common/header.inc.php");
?>

<body class="main_page">

<div id="page_wrap">

    <!-- BEGIN TOP BAR -->
    <?php include_once("../html/common/topbar_new.inc.php"); ?>
    <!-- END TOP BAR -->

    <div id="page_layout">
        <div id="page_body">
            <div id="wrap3">
                <div id="wrap2">
                    <div id="wrap1">
                        <div id="content">
                            <div class="note orange">
                                <div class="title">Success!</div>
                                DB update success!
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- BEGIN FOOTER -->
            <?php include_once("../html/common/footer_new.inc.php"); ?>
            <!-- END FOOTER -->

        </div>
    </div>

</body>
</html>