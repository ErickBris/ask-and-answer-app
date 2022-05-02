<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    if (auth::isSession()) {

        header("Location: /admin-panel/main");
    }

    $page_id = "login";

    $user_username = '';

    $error = false;
    $error_message = '';

    if (!empty($_POST)) {

        $user_username = isset($_POST['user_username']) ? $_POST['user_username'] : '';
        $user_password = isset($_POST['user_password']) ? $_POST['user_password'] : '';
        $token = isset($_POST['authenticity_token']) ? $_POST['authenticity_token'] : '';

        $user_username = helper::clearText($user_username);
        $user_password = helper::clearText($user_password);

        $user_username = helper::escapeText($user_username);
        $user_password = helper::escapeText($user_password);

        if (auth::getAuthenticityToken() !== $token) {

            $error = true;
            $error_message = 'Error!';
        }

        if (!$error) {

            $access_data = array();

            $account = new account($dbo);
            $access_data = $account->signin($user_username, $user_password);

            if ($access_data['error'] === false) {

                $clientId = 0; // Desktop version

                $access_data = $auth->create($access_data['accountId'], $clientId);

                if ($access_data['error'] === false) {

                    if ($account->getAccessLevel($access_data['accountId']) != 0 ) {

                        auth::setSession($access_data['accountId'], $user_username, $account->getAccessLevel($access_data['accountId']), $access_data['accessToken']);
                        auth::updateCookie($user_username, $access_data['accessToken']);

                        header("Location: /admin-panel/main");
                    }
                }

            } else {

                $error = true;
                $error_message = 'Incorrect login or password.';
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

            <div id="page_auth">

                <div class="header">
                    <div class="title">
                        <?php echo $LANG['page-login']; ?>
                    </div>
                </div>


                <div class="error <?php if (!$error) echo "hide"; ?>">
                    <?php echo $error_message; ?>
                </div>

                <div class="frm">
                    <form action="/admin-panel/login" method="post" id="login_form">
                        <input autocomplete="off" type="hidden" name="authenticity_token" value="<?php echo auth::getAuthenticityToken(); ?>">
                        <div class="frm_header">
                            <label class="noselect" for="user_username"><?php echo $LANG['label-username']; ?>:</label>
                        </div>
                        <input autocomplete="off" type="text" id="user_username" class="frm_input" maxlength="24" name="user_username" value="<?php echo stripslashes($user_username); ?>">
                        <div class="frm_header">
                            <label class="noselect" for="user_password"><?php echo $LANG['label-password']; ?>:</label>
                        </div>
                        <input autocomplete="off" type="password" id="user_password" class="frm_input" maxlength="20" name="user_password" value="">
                        <div class="">
                            <button type="submit" class="frm_btn primary_btn big_btn"><?php echo $LANG['action-login']; ?></button>
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