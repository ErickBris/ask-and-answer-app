<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

    $questions = new questions($dbo);

    $ua = array("Коли останній раз ви були в кінотеатрі? Який фільм дивилися?",
                "Чим закінчується твій день?",
                "Щоб ви написали на запітнілому склі?",
                "За яку образу ніколи не пробачиш навіть дорогу тобі людини?",
                "Ви вмієте плавати? Як навчилися?",
                "Як позбутися черг?",
                "Що ви вмієте смажити найкраще?",
                "Любов - це свобода чи клітка?",
                "Що таке дежавю?",
                "Як ви зустрічаєте весну?",
                "Хто або що приносить у твоє життя яскраві фарби?",
                "Як багато для тебе означають слова?",
                "Який фрукт найкорисніший?",
                "Любиш міцну каву?",
                "Який автомобіль ви б хотіли придбати?",
                "Як ви ставитеся до вегетаріанців?",
                "Якому виду транспорту віддаєш перевагу?",
                "Який подарунок ви ніколи не забудете?",
                "Що у тебе краще виходить: створювати чи руйнувати?",
                "Самая даремна в світі річ?");

    $count = count($ua);

    for ( $i = 0; $i < $count; $i++ )  {

        $questions->add_to_db( $ua[$i], "ua" );
    }

    $en = array("When was the last time you were in a movie theater? What movie looked?",
                "Who you very much hate?",
                "Often kidding?",
                "Blonde or brunette?",
                "Do you believe in God?",
                "Your main flaw?",
                "Which comedy series do you like best?",
                "Can I call you a patient person? In what ways?",
                "Why do such simple things as difficult to explain?",
                "The three most common reasons why relationships are broken?",
                "Would you rather live without TV or music?",
                "What matters to you most - money, good looks or attitude?",
                "Which was the funniest proposal you got?",
                "What problems did you have in school?",
                "Do you believe that dreams can sometimes predict the future?",
                "How do you like to celebrate your birthday?",
                "What was the last movie that disappointed you?",
                "If you had $5 left in your pocket what would you spend it on?",
                "What is the craziest thing you have ever done for money?",
                "Which is the most memorable day you have ever had?",
                "Do you believe that dreams can sometimes predict the future?",
                "What's the worst nightmare you've ever had?",
                "Would you leave your home country for the one you love?",
                "Who was the last person you kissed?",
                "What do you think is the most enjoyable thing?",
                "Who do you respect the most?",
                "What do you know that others don’t?",
                "What would the world be like without religion?",
                "Are you happy with the amount of information in your head?",
                "What are the things you want to avoid in a relationship?",
                "What's your favorite movie snack?",
                "What is in fashion today?",
                "What would you do if you meet an alien?",
                "What is the most beautiful language?",
                "Name any 3 things that make you smile?",
                "What do you put on your pizza?",
                "Which is the funniest name you've ever heard?",
                "Do you tolerate heat or cold better?",
                "Where do you see yourself five years from now?",
                "What kind of music calms you down?",
                "Which websites do you visit most frequently?");

    $count = count($en);

    for ( $i = 0; $i < $count; $i++ )  {

        $questions->add_to_db( $en[$i], "en" );
    }

    $ru = array("Когда последний раз вы были в кинотеатре? Какой фильм смотрели?",
                "Кого ты очень сильно ненавидишь?",
                "Часто обманываешь?",
                "Блондинки или брюнетки?",
                "Веришь в Бога?",
                "Твой главный недостаток?",
                "Какой комедийный сериал вам нравится больше всего?",
                "Можно ли назвать тебя терпеливым человеком? В чем это выражается?",
                "Почему такие простые вещи объяснить так сложно?",
                "Три самые частые причины, по которым рвутся отношения?",
                "Сколько времени тебе нужно, чтобы собраться утром?",
                "Всегда добиваешься своей цели?",
                "Как ты относишься к слухам?",
                "Какую песню ты можешь слушать бесконечно?",
                "Самая бесполезная в мире вещь?");

    $count = count($ru);

    for ( $i = 0; $i < $count; $i++ )  {

        $questions->add_to_db( $ru[$i], "ru" );
    }

?>