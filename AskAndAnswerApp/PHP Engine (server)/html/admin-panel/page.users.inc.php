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

    $page_id = "search";

    $error = false;
    $error_message = '';
    $query = '';
    $result = array();
    $result['createAt'] = 0;
    $result['users'] = array();

    if (isset($_GET['query'])) {

        $query = isset($_GET['query']) ? $_GET['query'] : '';

        $query = helper::clearText($query);
        $query = helper::escapeText($query);

        if (strlen($query) > 3) {

            $search = new search($dbo);
            $result = $search->query(auth::getCurrentUserId(), $query);
        }
    }

    auth::newAuthenticityToken();

    $css_files = array("sprosi.css");
    $page_title = $LANG['page-users'];

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
                                <input placeholder="<?php echo $LANG['placeholder-users-search']; ?>" name="query" style="width: 805px" type="text" value="<?php echo stripslashes($query); ?>">
                                <button class="primary_btn big_btn right" style="width: 120px"><?php echo $LANG['action-search']; ?></button>
                            </form>

                            <?php

                                function drawUsersTable($result, $LANG) {

                                    ?>

                                        <table class="admin_table">
                                            <tr>
                                                <th class="text-left"><?php echo $LANG['label-id']; ?></th>
                                                <th><?php echo $LANG['label-fullname']; ?></th>
                                                <th><?php echo $LANG['label-username']; ?></th>
                                                <th><?php echo $LANG['label-action']; ?></th>
                                            </tr>
                                    <?php

                                        foreach ( $result['users'] as $key => $value) {

                                            ?>

                                            <tr>
                                                <td class="text-left"><?php echo $value['id']; ?></td>
                                                <td><?php echo $value['fullname'];; ?></td>
                                                <td><?php echo $value['username'];; ?></td>
                                                <td><a href="/admin-panel/profile?id=<?php echo $value['id']; ?>"><?php echo $LANG['action-edit-profile']; ?></a></td>
                                            </tr>

                                            <?php
                                        }

                                    ?>

                                        </table>

                                    <?php
                                }

                                if ( $result['createAt'] != 0 ) {

                                    ?>

                                        <div class="box_code">
                                            Found: <?php echo $result['itemCount']; ?>
                                        </div>

                                    <?php
                                }

                                if ( count($result['users']) > 0 ) {

                                    drawUsersTable($result, $LANG);

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