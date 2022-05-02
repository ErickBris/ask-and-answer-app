<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $css_files = array("sprosi.css");
    $page_title = $profileInfo['fullname']." | ".APP_HOST."/".$profileInfo['username'];

    include_once("../html/common/header.inc.php");

?>

<body class="bg_gray">

<div id="page_wrap">

    <?php

        include_once("../html/common/topbar_new.inc.php");
    ?>

    <div id="page_layout" class="profile_page">

        <?php

            include_once("../html/common/banner.inc.php");
        ?>

        <div id="page_body">
            <div id="wrap3">
                <div id="wrap2">
                    <div id="wrap1">

                        <div class="profile_wrap">
                            <a href="/<?php echo $profileInfo['username']; ?>" class="profile_img_wrap">
                                <img class="user_image" src="/img/profile_default_photo.png">
                            </a>
                            <div class="profile_info_wrap">
                                <div class="user_header">
                                    <?php echo $profileInfo['fullname']; ?>
                                    <span class="page_verified"></span>
                                </div>
                                <div class="user_username">
                                    <?php echo $profileInfo['username']; ?>
                                </div>

                            </div>
                        </div>

                        <div id="content">

                            <div class="info">
                                <?php

                                    switch ($profileInfo['state']) {

                                        case ACCOUNT_STATE_DISABLED: {

                                            // User disable account
                                            echo $LANG['label-account-disabled'];
                                            break;
                                        }

                                        case ACCOUNT_STATE_BLOCKED: {

                                            // Account blocked by moderator
                                            echo $LANG['label-account-blocked'];
                                            break;
                                        }

                                        default: {

                                            // Account created and not activated
                                            echo $LANG['label-account-deactivated'];
                                            break;
                                        }
                                    }
                                ?>
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

</body>
</html>