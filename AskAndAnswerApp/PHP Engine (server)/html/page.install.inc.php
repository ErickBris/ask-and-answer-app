<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $stats = new stats($dbo);

    if ($stats->getUsersCount() > 0) {

        header("Location: /admin-panel/login");
    }

    if (auth::isSession()) {

        header("Location: /admin-panel/main");
    }

    include_once("../sys/core/initialize.inc.php");

    $page_id = "install";

    $error = false;
    $error_message = array();

    $user_username = '';
    $user_fullname = '';
    $user_password = '';
    $user_password_repeat = '';
    $user_email = '';
    $social_id = 0;
    $signup_with = '';

    $error_token = false;
    $error_username = false;
    $error_fullname = false;
    $error_password = false;
    $error_password_repeat = false;
    $error_email = false;

    if (!empty($_POST)) {

        $error = false;

        $user_username = isset($_POST['user_username']) ? $_POST['user_username'] : '';
        $user_fullname = isset($_POST['user_fullname']) ? $_POST['user_fullname'] : '';
        $user_password = isset($_POST['user_password']) ? $_POST['user_password'] : '';
        $user_email = isset($_POST['user_email']) ? $_POST['user_email'] : 0;
        $token = isset($_POST['authenticity_token']) ? $_POST['authenticity_token'] : '';

        $user_username = helper::clearText($user_username);
        $user_fullname = helper::clearText($user_fullname);
        $user_password = helper::clearText($user_password);
        $user_password_repeat = helper::clearText($user_password_repeat);
        $user_email = helper::clearText($user_email);

        $user_username = helper::escapeText($user_username);
        $user_fullname = helper::escapeText($user_fullname);
        $user_password = helper::escapeText($user_password);
        $user_password_repeat = helper::escapeText($user_password_repeat);
        $user_email = helper::escapeText($user_email);

        if (auth::getAuthenticityToken() !== $token) {

            $error = true;
            $error_token = true;
            $error_message[] = 'Error!';
        }

        if (!helper::isCorrectLogin($user_username)) {

            $error = true;
            $error_username = true;
            $error_message[] = 'Incorrect username.';
        }

        if ($helper->isLoginExists($user_username)) {

            $error = true;
            $error_username = true;
            $error_message[] = 'Username already taken!';
        }

        if (!helper::isCorrectFullname($user_fullname)) {

            $error = true;
            $error_fullname = true;
            $error_message[] = 'Incorrect fullname.';
        }

        if (!helper::isCorrectPassword($user_password)) {

            $error = true;
            $error_password = true;
            $error_message[] = 'Incorrect password.';
        }

        if (!helper::isCorrectEmail($user_email)) {

            $error = true;
            $error_email = true;
            $error_message[] = 'Incorrect email.';
        }

        if ($helper->isEmailExists($user_email)) {

            $error = true;
            $error_email = true;
            $error_message[] = 'User with this email is exists in database.';
        }

        if (!$error) {

            $account = new account($dbo);

            $result = array();
            $result = $account->signup($user_username, $user_fullname, $user_password, $user_email, $social_id, $signup_with);

            if ($result['error'] === false) {

                $access_data = $account->signin($user_username, $user_password);

                if ($access_data['error'] === false) {

                    $clientId = 0; // Desktop version

                    $access_data = $auth->create($access_data['accountId'], $clientId);

                    if ($access_data['error'] === false) {

                        include_once("../html/install/init.inc.php");

                        $account->setId($access_data['accountId']);

                        $account->setAccessLevel(7);
                        $account->setState(ACCOUNT_STATE_ENABLED);

                        auth::setSession($access_data['accountId'], $user_username, $account->getAccessLevel($access_data['accountId']), $access_data['accessToken']);
                        auth::updateCookie($user_username, $access_data['accessToken']);;

                        header("Location: /admin-panel/main");
                    }

                    header("Location: /install");
                }
            }
        }
    }

    auth::newAuthenticityToken();

    $css_files = array("sprosi.css");
    $page_title = APP_TITLE;

    include_once("../html/common/header.inc.php");
?>

<body>

    <div id="page_wrap">

        <?php

            include_once("../html/common/topbar_new.inc.php");
        ?>

        <div id="page_layout">

            <?php

                include_once("../html/common/banner.inc.php");
            ?>

            <div id="page_body" class="banner">
                <div id="wrap3">
                    <div id="wrap2">
                        <div id="wrap1">
                            <div id="content">

                                <div class="note orange">
                                    <div class="title">Warning</div>
                                    Remember that now Create an account administrator! You created your account administrator will also be available as an account!
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div id="page_auth">

                <div class="header">
                    <div class="title">
                        <?php echo $LANG['page-install']; ?>
                    </div>
                </div>

                <div class="error <?php if (!$error) echo "hide"; ?>">
                    <?php

                        foreach ( $error_message as $msg ) {

                            echo $msg . "<br />";
                        }
                    ?>
                </div>

                <div class="frm">
                    <form action="/install" method="post" id="login_form">
                        <input autocomplete="off" type="hidden" name="authenticity_token" value="<?php echo auth::getAuthenticityToken(); ?>">
                        <div class="frm_header">
                            <label class="noselect" for="user_username"><?php echo $LANG['label-username']; ?>:</label>
                        </div>
                        <input autocomplete="off" type="text" id="user_username" class="frm_input" maxlength="24" name="user_username" value="<?php echo stripslashes($user_username); ?>">
                        <div class="frm_header">
                            <label class="noselect" for="user_fullname"><?php echo $LANG['label-fullname']; ?>:</label>
                        </div>
                        <input autocomplete="off" type="text" id="user_fullname" class="frm_input" maxlength="24" name="user_fullname" value="<?php echo stripslashes($user_fullname); ?>">
                        <div class="frm_header">
                            <label class="noselect" for="user_password"><?php echo $LANG['label-password']; ?>:</label>
                        </div>
                        <input autocomplete="off" type="password" id="user_password" class="frm_input" maxlength="20" name="user_password" value="">
                        <div class="frm_header">
                            <label class="noselect" for="user_email"><?php echo $LANG['label-email']; ?>:</label>
                        </div>
                        <input autocomplete="off" type="text" id="user_email" class="frm_input" maxlength="24" name="user_email" value="<?php echo stripslashes($user_email); ?>">
                        <div class="">
                            <button type="submit" class="frm_btn primary_btn big_btn"><?php echo $LANG['action-install']; ?></button>
                        </div>
                    </form>
                </div>

            </div>

            <?php

                include_once("../html/common/footer_new.inc.php");
            ?>

        </div>
    </div>

</body>
</html>