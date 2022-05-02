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

        ?>

            <div id="page_topbar">

                <div class="topbar">
                    <div class="content">
                        <a href="/" class="logo"></a>
                        <a href="/admin-panel/login" class="topbar_item"><?php echo $LANG['topbar-signin']; ?></a>
                    </div>
                </div>

            </div>
        <?php

    } else {

        ?>

            <div id="page_topbar">

                <div class="topbar">
                    <div class="content">
                        <a href="/admin-panel/main" class="logo"></a>
                        <a href="/admin-panel/main" class="topbar_item"><?php echo $LANG['topbar-stats']; ?></a>
                        <a href="/admin-panel/questions" class="topbar_item"><?php echo $LANG['topbar-questions']; ?></a>
                        <a href="/admin-panel/users" class="topbar_item"><?php echo $LANG['topbar-users']; ?></a>
                        <a href="/admin-panel/logout/?access_token=<?php echo auth::getAccessToken(); ?>&continue=/" class="topbar_item"><?php echo $LANG['topbar-logout']; ?></a>
                    </div>
                </div>

            </div>
        <?php
    }
?>