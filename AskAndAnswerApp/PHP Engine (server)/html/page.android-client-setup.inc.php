<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $page_id = "app_setup";

    $css_files = array("sprosi.css");
    $page_title = $LANG['page-app-setup']." | ".APP_TITLE;

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
                                        <span><?php echo $LANG['page-app-setup']; ?></span>
                                    </div>
                                </div>

                                <h3><b>Configure the application QA Script for Android is quick and easy!</b></h3>

                                <div class="note orange">
                                    <div class="title">Warning!</div>
                                    Application QA Script for Android created with Android Studio.
                                </div>

                                <p><b>1. Open the project in the editor (preferably in Android Studio). You may need to adjust the Android SDK Location in the Project Structure (Ctrl + Alt + Shift + S).</b></p>
                                <p><b>2. Change API_DOMAIN on your domain (file Constants.java). Also, you can change the values: CLIENT_ID, APP_NAME, APP_VERSION, APP_YEAR, APP_COPYRIGHT.</b></p>
                                <div>
                                    <img alt="QA Script application for Android setup in Android Studio" title="QA Script application for Android setup in Android Studio" style="width: 100%;" src="/img/prepare_android_app.png"/>
                                </div>

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