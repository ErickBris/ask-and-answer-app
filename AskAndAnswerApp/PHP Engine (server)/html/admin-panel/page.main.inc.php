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

    $stats = new stats($dbo);

    $page_id = "main";

    $error = false;
    $error_message = '';

    auth::newAuthenticityToken();

    $css_files = array("sprosi.css");
    $page_title = $LANG['page-main'];

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
                                    <span><?php echo $LANG['label-stats']; ?></span>
                                </div>
                            </div>

                            <table class="admin_table">
                                <tr>
                                    <th class="text-left"><?php echo $LANG['label-category']; ?></th>
                                    <th><?php echo $LANG['label-count']; ?></th>
                                </tr>
                                <tr>
                                    <td class="text-left"><?php echo $LANG['admin-users']; ?></td>
                                    <td><?php echo $stats->getUsersCount(); ?></td>
                                </tr>
                                <tr>
                                    <td class="text-left"><?php echo $LANG['admin-questions']; ?></td>
                                    <td><?php echo $stats->getQuestionsCount(); ?></td>
                                </tr>
                                <tr>
                                    <td class="text-left"><?php echo $LANG['admin-answers']; ?></td>
                                    <td><?php echo $stats->getAnswersCount(); ?></td>
                                </tr>
                                <tr>
                                    <td class="text-left"><?php echo $LANG['admin-likes']; ?></td>
                                    <td><?php echo $stats->getLikesCount(); ?></td>
                                </tr>
                                <tr>
                                    <td class="text-left"><?php echo $LANG['admin-user-reports']; ?></td>
                                    <td><?php echo $stats->getProfileAbuseReports(); ?></td>
                                </tr>
                            </table>

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