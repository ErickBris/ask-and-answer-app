<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $page_id = "error";

    $css_files = array("sprosi.css");
    $page_title = $LANG['page-error-404']." | ".APP_TITLE;

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

                                <div class="info">
                                    <div><?php echo $LANG['label-error-404']; ?></div>
                                    <a href="/"><?php echo $LANG['action-back-to-main-page']; ?></a>
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