<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $profileId = $helper->getUserId($request[0]);

    $profile = new profile($dbo, $profileId);

    $profile->setRequestFrom(auth::getCurrentUserId());
    $profileInfo = $profile->get();

    if ($profileInfo['error'] === true) {

        include_once("../html/error.inc.php");
        exit;
    }

    if ( $profileInfo['state'] != ACCOUNT_STATE_ENABLED ) {

        include_once("../html/stubs/profile.inc.php");
        exit;
    }

    $profilePhotoUrl = APP_URL."/img/profile_default_photo.png";

    if ( strlen($profileInfo['bigPhotoUrl']) != 0 ) {

        $profilePhotoUrl = $profileInfo['bigPhotoUrl'];
    }

    auth::newAuthenticityToken();

    $page_id = "profile";

    $css_files = array("sprosi.css", "tipsy.css");
    $page_title = $profileInfo['fullname']." | ".APP_HOST."/".$profileInfo['username'];

    include_once("../html/common/header.inc.php");

    if (strlen($profileInfo['originCoverUrl']) != 0) {

        ?>
            <style type="text/css">

                body {
                    background-color:#00b1db;
                    background-attachment:fixed;
                    background-repeat:repeat;
                    background-position:left top;
                    background-image:url(<?php echo $profileInfo['originCoverUrl']; ?>);}
            </style>
        <?php
    }
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

                                <img class="user_image" src="<?php echo $profilePhotoUrl; ?>">
                            </a>

                            <div class="profile_info_wrap">

                                <div class="profile_data_wrap">
                                    <div class="user_header">
                                        <a href="/<?php echo $profileInfo['username']; ?>"><?php echo $profileInfo['fullname']; ?></a>
                                        <?php

                                            if ( $profileInfo['verify'] == 1 ) {

                                                ?>
                                                    <span class="page_verified"></span>
                                                <?php
                                            }
                                        ?>
                                    </div>

                                    <div class="user_username">
                                        @<?php echo $profileInfo['username']; ?>
                                    </div>

                                    <div class="user_status">

                                    </div>
                                </div>

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
