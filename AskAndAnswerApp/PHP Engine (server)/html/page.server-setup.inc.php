<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $page_id = "script_setup";

    $css_files = array("sprosi.css");
    $page_title = $LANG['page-script-setup']." | ".APP_TITLE;

    include_once("../html/common/header.inc.php");

    ?>

<body class="bg_gray">

    <div id="page_wrap">

        <?php

            include_once("../html/common/topbar_new.inc.php");
        ?>

        <div id="page_layout">

            <?php

                include_once("../html/common/banner.inc.php");
            ?>

            <div id="page_body">
                <div id="wrap3">
                    <div id="wrap2">
                        <div id="wrap1">
                            <div id="content">

                                <div class="header">
                                    <div class="title">
                                        <span><?php echo $LANG['page-script-setup']; ?></span>
                                    </div>
                                </div>

                                <h3><b>Configure the server part of the QA Script with fast three easy steps!</b></h3>

                                <p><b>1. Upload the script to the root server.</b></p>
                                <p><b>2. Open and edit /sys/config/db.inc.php</b></p>
                                <div class="note orange">
                                    <div class="title">Warning!</div>
                                    You should always register data for the database: host, user, password and db name.
                                    <img src="/img/prepare.png"/>
                                </div>
                                <p><b>3. Open in browser: http://your_site.com/install</b></p>
                                <div class="note orange">
                                    <div class="title">Warning!</div>
                                    Remember that now Create an account administrator! You created your account administrator will also be available as an account!
                                </div>

                                <p><b>Configure the server part of the script?</b> <a href="/android-client-setup">Learn how to configure the application QA script for Android!</a></p>

                            </div>
                        </div>
                    </div>
                </div>
            </div>

        <?php

            include_once("../html/common/footer_new.inc.php");
        ?>

        </div>
    </div>

</body>
</html>