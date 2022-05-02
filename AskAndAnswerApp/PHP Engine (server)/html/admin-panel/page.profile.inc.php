<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    if (!auth::isSession()) {

        header("Location: /admin-panel/login");
    }

    if (isset($_GET['id'])) {

        $profileId = isset($_GET['id']) ? $_GET['id'] : 0;

        $profileId = helper::clearInt($profileId);
    }

    $profile = new profile($dbo, $profileId);

    $profile->setRequestFrom(auth::getCurrentUserId());
    $profileInfo = $profile->get();

    if ($profileInfo['error'] === true) {

        include_once("../html/error.inc.php");
        exit;
    }

    if (isset($_GET['act'])) {

        $act = isset($_GET['act']) ? $_GET['act'] : '';
        $token = isset($_GET['access_token']) ? $_GET['access_token'] : '';

        $act = helper::escapeText($act);

        if (auth::getAccessToken() === $token && !APP_DEMO) {

            switch ($act) {

                case "delete-photo" : {

                    $photos = array('originPhotoUrl' => '',
                                    'normalPhotoUrl' => '',
                                    'bigPhotoUrl' => '',
                                    'lowPhotoUrl' => '');

                    $account = new account($dbo, $profileId);
                    $account->setPhoto($photos);

                    header("Location: /admin-panel/profile?id=".$profileId);
                    break;
                }

                case "delete-cover" : {

                    $covers = array('originCoverUrl' => '',
                                    'normalCoverUrl' => '');

                    $account = new account($dbo, $profileId);
                    $account->setCover($covers);

                    header("Location: /admin-panel/profile?id=".$profileId);
                    break;
                }

                default: {

                    header("Location: /admin-panel/profile?id=".$profileId);
                }
            }
        }

        header("Location: /admin-panel/profile?id=".$profileId);
    }

    $myPage = false;

    if (auth::getCurrentUserId() == $profileInfo['id']) {

        $myPage = true;
    }

    $page_id = "profile";

    $error = false;
    $success = false;
    $error_message = '';

    if (!empty($_POST)) {

        $error = true;

        $username = isset($_POST['username']) ? $_POST['username'] : '';
        $fullname = isset($_POST['fullname']) ? $_POST['fullname'] : '';
        $verify = isset($_POST['verify']) ? $_POST['verify'] : 0;
        $state = isset($_POST['state']) ? $_POST['state'] : 0;
        $token = isset($_POST['authenticity_token']) ? $_POST['authenticity_token'] : '';

        $username = helper::clearText($username);
        $fullname = helper::clearText($fullname);
        $verify = helper::clearInt($verify);
        $state = helper::clearInt($state);

        $username = helper::escapeText($username);
        $fullname = helper::escapeText($fullname);

        if (auth::getAuthenticityToken() === $token && !APP_DEMO) {

            $account = new account($dbo, $profileId);

            $account->setFullname($fullname);
            $account->setVerify($verify);
            $account->setState($state);

            $success = true;
        }

        header("Location: /admin-panel/profile?id=".$profileId);
    }

    $profile = new profile($dbo, $profileId);

    $profile->setRequestFrom(auth::getCurrentUserId());
    $profileInfo = $profile->get();

    auth::newAuthenticityToken();

    $css_files = array("sprosi.css");
    $page_title = stripslashes($profileInfo['fullname']);

    include_once("../html/common/header.inc.php");

    if ( strlen($profileInfo['originCoverUrl']) != 0 ) {

        ?>

            <style type="text/css">

                body { background-color:#00b1db;
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

                            <?php

                                $profilePhotoUrl = "/img/profile_default_photo.png";
                                $photo = '';

                                if ( strlen($profileInfo['bigPhotoUrl']) != 0 ) {

                                    $profilePhotoUrl = $profileInfo['bigPhotoUrl'];
                                }
                            ?>

                            <span class="profile_img_wrap">
                                <img class="user_image" src="<?php echo $profilePhotoUrl; ?>">
                            </span>

                            <div class="profile_info_wrap">

                                <div class="user_header">
                                    <?php

                                        echo $profileInfo['fullname'];

                                        if ( $profileInfo['verify'] == 1 ) {

                                            ?>
                                                <span class="page_verified"></span>
                                            <?php
                                        }
                                    ?>
                                </div>

                                <div class="user_username">
                                    <?php echo $profileInfo['username']; ?>
                                </div>

                                <div class="user_status">

                                </div>

                                <div class="user_actions">
                                    <?php

                                        if ( strlen($profileInfo['bigPhotoUrl']) != 0 ) {

                                            ?>
                                                <a href="/admin-panel/profile?id=<?php echo $profileId; ?>&act=delete-photo&access_token=<?php echo auth::getAccessToken(); ?>" class="flat_btn"><?php echo $LANG['action-delete-profile-photo']; ?></a>
                                            <?php
                                        }

                                        if ( strlen($profileInfo['normalCoverUrl']) != 0 ) {

                                            ?>
                                                <a href="/admin-panel/profile?id=<?php echo $profileId; ?>&act=delete-cover&access_token=<?php echo auth::getAccessToken(); ?>" class="flat_btn"><?php echo $LANG['action-delete-profile-cover']; ?></a>
                                            <?php
                                        }
                                    ?>
                                </div>

                            </div>
                        </div>

                        <div class="profile_form_wrap">



                        </div>

                        <div id="content">

                            <div class="header">
                                <div class="title">
                                    <span><?php echo $LANG['action-edit-profile']; ?></span>
                                </div>
                            </div>

                            <form method="post" class="support_wrap">

                                <input autocomplete="off" type="hidden" name="authenticity_token" value="<?php echo auth::getAuthenticityToken(); ?>">

                                <div class="code <?php if (!$success) echo "hide"; ?>">
                                    Profile successfully edited.
                                </div>

                                <div class="ticket_email">
                                    <label for="username" class="noselect"><?php echo $LANG['label-username']; ?></label>
                                    <input type="text" id="username" disabled="disabled" placeholder="" name="username" maxlength="64" value="<?php echo $profileInfo['username']; ?>">
                                </div>

                                <div class="ticket_title">
                                    <label for="fullname" class="noselect"><?php echo $LANG['label-fullname']; ?></label>
                                    <input type="text" id="fullname" placeholder="" name="fullname" maxlength="164" value="<?php echo $profileInfo['fullname']; ?>">
                                </div>

                                <div class="ticket_title">
                                    <label for="verify" class="noselect"><?php echo $LANG['label-verify']; ?></label>
                                    <select name="verify">
                                        <option <?php if ($profileInfo['verify'] == 0) echo "selected=\"selected\"" ?> value="0"><?php echo $LANG['label-false']; ?></option>
                                        <option <?php if ($profileInfo['verify'] == 1) echo "selected=\"selected\"" ?> value="1"><?php echo $LANG['label-true']; ?></option>
                                    </select>
                                </div>

                                <div class="ticket_title">
                                    <label for="state" class="noselect"><?php echo $LANG['label-state']; ?></label>
                                    <select name="state">
                                        <option <?php if ($profileInfo['state'] == ACCOUNT_STATE_ENABLED) echo "selected=\"selected\"" ?> value="0">Account is active</option>
                                        <option <?php if ($profileInfo['state'] == ACCOUNT_STATE_DISABLED) echo "selected=\"selected\"" ?> value="1">Account has been deactivated by the user</option>
                                        <option <?php if ($profileInfo['state'] == ACCOUNT_STATE_BLOCKED) echo "selected=\"selected\"" ?> value="2">Account has been blocked by the administrator</option>
                                        <option <?php if ($profileInfo['state'] == ACCOUNT_STATE_DEACTIVATED) echo "selected=\"selected\"" ?> value="3">Account is created, but not yet activated by the user</option>
                                    </select>
                                </div>

                                <div class="ticket_controls">
                                    <button class="primary_btn big_btn"><?php echo $LANG['action-save']; ?></button>
                                </div>

                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <?php

            include_once("../html/common/footer_new.inc.php");
        ?>

        <script type="text/javascript">

            $("textarea[name=questionText]").autosize();

            $("textarea[name=questionText]").bind('keyup mouseout', function() {

                var max_char = 300;

                var count = $("textarea[name=questionText]").val().length;

                $("span#question_word_counter").empty();
                $("span#question_word_counter").html(max_char - count);

                event.preventDefault();
            });

        </script>

    </div>

</body>
</html>