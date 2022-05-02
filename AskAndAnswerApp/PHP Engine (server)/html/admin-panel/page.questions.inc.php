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

    $page_id = "questions";

    $error = false;
    $error_message = '';
    $query = '';
    $result = array();
    $result['addedToListAt'] = 0;
    $result['questions'] = array();

    if (isset($_GET['query'])) {

        $query = isset($_GET['query']) ? $_GET['query'] : '';
        $addedToListAt = isset($_GET['addedToListAt']) ? $_GET['addedToListAt'] : 0;

        $query = helper::clearText($query);
        $query = helper::escapeText($query);

        if (strlen($query) > 3) {

            $search = new questions($dbo);
            $result = $search->query($query, $addedToListAt);
        }
    }

    auth::newAuthenticityToken();

    $css_files = array("sprosi.css");
    $page_title = $LANG['page-questions'];

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
                                    <span><?php echo $LANG['page-search']; ?></span>
                                </div>
                            </div>

                            <form class="support_wrap" method="get" style="margin-bottom: 20px">
                                <input placeholder="<?php echo $LANG['placeholder-questions-search']; ?>" name="query" style="width: 805px" type="text" value="<?php echo stripslashes($query); ?>">
                                <button class="primary_btn big_btn right" style="width: 120px"><?php echo $LANG['action-search']; ?></button>
                            </form>

                            <?php

                            function drawResultTable($result, $dbo, $LANG) {

                                ?>

                                <table class="admin_table">
                                    <tr>
                                        <th class="text-left"><?php echo $LANG['label-id']; ?></th>
                                        <th class="text-left"><?php echo $LANG['label-from-user']; ?></th>
                                        <th class="text-left"><?php echo $LANG['label-to-user']; ?></th>
                                        <th class="text-left"><?php echo $LANG['label-question']; ?></th>
                                        <th class="text-left"><?php echo $LANG['label-date']; ?></th>
                                        <th><?php echo $LANG['label-action']; ?></th>
                                    </tr>
                                    <?php

                                    foreach ( $result['questions'] as $key => $value) {

                                        $profile = new profile($dbo, $value['toUserId']);

                                        $profileInfo = $profile->get();

                                        ?>

                                        <tr>
                                            <td class="text-left"><?php echo $value['id']; ?></td>
                                            <td class="text-left"><?php if ($value['fromUserId'] != 0 ) echo "<a href=\"/admin-panel/profile?id={$value['fromUserId']}\">".$value['fromUserFullname']."</a>"; else echo "-"; ?></td>
                                            <td class="text-left"><a href="/admin-panel/profile?id=<?php echo $value['toUserId']; ?>"><?php echo $profileInfo['fullname']; ?></a></td>
                                            <td class="text-left" style="word-break: break-all;"><?php echo $value['question']; ?></td>
                                            <td class="text-left" style="white-space: nowrap;"><?php echo date("Y-m-d H:i:s", $value['createAt']); ?></td>
                                            <td><a href="/admin-panel/question?id=<?php echo $value['id']; ?>"><?php echo $LANG['action-edit']; ?></a></td>
                                        </tr>

                                    <?php
                                    }

                                    ?>

                                </table>

                            <?php
                            }

                            if ( $result['addedToListAt'] != 0 ) {

                                ?>

                                <div class="box_code">
                                    Found: <?php echo count($result['questions']); ?>
                                </div>

                            <?php
                            }

                            if ( count($result['questions']) > 0 ) {

                                drawResultTable($result, $dbo, $LANG);

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