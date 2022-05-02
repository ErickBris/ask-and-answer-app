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

        $questionId = isset($_GET['id']) ? $_GET['id'] : 0;

        $questionId = helper::clearInt($questionId);
    }

    $question = new questions($dbo);
    $question->setRequestFrom(auth::getCurrentUserId());

    $questionInfo = $question->info($questionId);

    if ($questionInfo['error'] === true) {

        include_once("../html/error.inc.php");
        exit;
    }

    if ( $questionInfo['replyAt'] != 0 ) {

        header("Location: /admin-panel/answer?id=".$questionInfo['id']);
    }

    if (isset($_GET['act'])) {

        $act = isset($_GET['act']) ? $_GET['act'] : '';
        $token = isset($_GET['access_token']) ? $_GET['access_token'] : '';

        if (auth::getAccessToken() === $token && !APP_DEMO) {

            switch ($act) {

                case "delete" : {

                    $question->delete($questionInfo['id']);

                    header("Location: /admin-panel/question?id=".$questionId);
                    break;
                }

                case "restore" : {

                    $question->restore($questionInfo['id']);

                    header("Location: /admin-panel/question?id=".$questionId);
                    break;
                }

                default: {

                    header("Location: /admin-panel/question?id=".$questionId);
                }
            }
        }

        header("Location: /admin-panel/question?id=".$questionId);
    }

    $page_id = "question";

    $error = false;
    $success = false;
    $error_message = '';

    if (!empty($_POST)) {

        $error = true;

        $questionText = isset($_POST['question']) ? $_POST['question'] : '';
        $token = isset($_POST['authenticity_token']) ? $_POST['authenticity_token'] : '';

        $questionText = helper::clearText($questionText);

        $questionText = str_replace(array("\r", "\n"), " ", $questionText);
        $questionText  = preg_replace('/\s+/', ' ', $questionText );

        $questionText = helper::escapeText($questionText);

        if (auth::getAuthenticityToken() === $token && !APP_DEMO) {

            $question->edit($questionInfo['id'], $questionText);

            header("Location: /admin-panel/question?id=".$questionId);
        }

        header("Location: /admin-panel/question?id=".$questionId);
    }

    $profile = new profile($dbo, $questionInfo['toUserId']);

    $profile->setRequestFrom(auth::getCurrentUserId());
    $profileInfo = $profile->get();

    auth::newAuthenticityToken();

    $css_files = array("sprosi.css");
    $page_title = $LANG['action-edit-question'];

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
                                    <span><?php echo $LANG['action-edit-question']; ?></span>
                                </div>
                            </div>

                            <?php

                                if ( $questionInfo['removeAt'] != 0 ) {

                                    ?>

                                        <div class="note orange">
                                            <div class="title"><?php echo $LANG['label-question-removed']; ?></div>
                                            <a href="/admin-panel/question?id=<?php echo $questionInfo['id']; ?>&act=restore&access_token=<?php echo auth::getAccessToken(); ?>"><?php echo $LANG['action-restore']; ?></a>
                                        </div>

                                    <?php

                                } else {

                                    ?>

                                        <form method="post" class="support_wrap">

                                            <input autocomplete="off" type="hidden" name="authenticity_token" value="<?php echo auth::getAuthenticityToken(); ?>">

                                            <div class="ticket_detailed">
                                                <label for="question" class="noselect"><?php echo $LANG['label-question']; ?></label>
                                                <textarea id="question" name="question" maxlength="800" style="overflow: hidden; word-wrap: break-word; resize: none; height: 94px;"><?php echo $questionInfo['question']; ?></textarea>
                                            </div>

                                            <div class="ticket_controls">
                                                <button class="primary_btn big_btn"><?php echo $LANG['action-save']; ?></button>
                                                <a class="flat_btn big_btn" href="/admin-panel/question?id=<?php echo $questionInfo['id']; ?>&act=delete&access_token=<?php echo auth::getAccessToken(); ?>"><?php echo $LANG['action-remove']; ?></a>
                                            </div>

                                        </form>

                                    <?php

                                }
                            ?>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <?php

            include_once("../html/common/footer_new.inc.php");
        ?>

        <script type="text/javascript">

            $("textarea[name=question]").autosize();

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