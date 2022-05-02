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

    if (isset($_GET['hash'])) {

        $hash = isset($_GET['hash']) ? $_GET['hash'] : '';

        $hash = helper::clearText($hash);
        $hash = helper::escapeText($hash);

        $restorePointInfo = $helper->getRestorePoint($hash);

        if ($restorePointInfo['error'] !== false) {

            header("Location: /");
        }

    } else {

        header("Location: /");
    }


    $error = false;
    $error_message = array();

    $user_password = '';
    $user_password_repeat = '';

    if (!empty($_POST)) {

        $error = false;

        $user_password = isset($_POST['user_password']) ? $_POST['user_password'] : '';
        $user_password_repeat = isset($_POST['user_password_repeat']) ? $_POST['user_password_repeat'] : '';
        $token = isset($_POST['authenticity_token']) ? $_POST['authenticity_token'] : '';

        $user_password = helper::clearText($user_password);
        $user_password_repeat = helper::clearText($user_password_repeat);

        $user_password = helper::escapeText($user_password);
        $user_password_repeat = helper::escapeText($user_password_repeat);

        if (auth::getAuthenticityToken() !== $token) {

            $error = true;
            $error_message[] = 'Error!';
        }

        if (!helper::isCorrectPassword($user_password)) {

            $error = true;
            $error_message[] = 'Incorrect password.';
        }

        if ($user_password != $user_password_repeat) {

            $error = true;
            $error_message[] = 'Passwords do not match.';
        }

        if (!$error) {

            $account = new account($dbo, $restorePointInfo['accountId']);

            $account->newPassword($user_password);
            $account->restorePointRemove();

            header("Location: /restore/success");
            exit;
        }
    }

    auth::newAuthenticityToken();

    $page_id = "restore";

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
                        <?php echo $LANG['page-restore']; ?>
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
                    <form action="/restore/?hash=<?php echo $hash; ?>" method="post" id="login_form">
                        <input autocomplete="off" type="hidden" name="authenticity_token" value="<?php echo auth::getAuthenticityToken(); ?>">

                        <div class="frm_header">
                            <label class="noselect" for="user_password"><?php echo $LANG['label-new-password']; ?>:</label>
                        </div>

                        <input autocomplete="off" type="password" id="user_password" class="frm_input" maxlength="20" name="user_password" value="">

                        <div class="frm_header">
                            <label class="noselect" for="user_password_repeat"><?php echo $LANG['label-repeat-password']; ?>:</label>
                        </div>

                        <input autocomplete="off" type="password" id="user_password_repeat" class="frm_input" maxlength="20" name="user_password_repeat" value="">

                        <div class="">
                            <button type="submit" class="frm_btn primary_btn big_btn"><?php echo $LANG['action-change']; ?></button>
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