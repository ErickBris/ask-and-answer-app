<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $page_id = "about";

    $css_files = array("sprosi.css");
    $page_title = $LANG['page-about']." | ".APP_TITLE;

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
                                        <span><?php echo $LANG['page-about']; ?></span>
                                    </div>
                                </div>

                                <h3><?php echo "QA Script © ".APP_YEAR ?> <a target="_blank" href="http://qascript.com.ua">qascript.com.ua</a></h3>
                                <h3>Contact the author QA Script: qascript@mail.ru</h3>

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