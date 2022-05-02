<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

?>

    <div id="page_footer">

        <div id="bottom_nav">
            <a href="/about"><?php echo $LANG['footer-about']; ?></a>
            <a href="/server-setup"><?php echo $LANG['footer-script-setup']; ?></a>
            <a href="/android-client-setup"><?php echo $LANG['footer-app-setup']; ?></a>

            <?php

                $stats = new stats($dbo);

                if ($stats->getUsersCount() == 0) {

                    ?>

                        <a href="/install"><?php echo $LANG['footer-install']; ?></a>
                    <?php
                }
            ?>
        </div>

        <div id="footer" class="clear">
            <?php echo APP_TITLE; ?> © <?php echo APP_YEAR; ?>
            <div>
                <a target="_blank" href="<?php echo COMPANY_URL; ?>"><?php echo APP_VENDOR; ?></a>
            </div>
        </div>

    </div>

    <script type="text/javascript" src="/js/jquery-2.1.1.js"></script>
    <script src="/js/common.js"></script>

    <script src="/js/jquery.colorbox.js?x=30"></script>
    <script src="/js/jquery.autosize.js?x=30"></script>