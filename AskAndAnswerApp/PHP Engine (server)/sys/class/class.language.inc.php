<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk, qascript@mail.ru
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

class language extends db_connect
{

    private $language;

    public function __construct($dbo = NULL, $language = "en")
    {
        parent::__construct($dbo);

        $this->set($language);

    }

    public function timeAgo($time)
    {
        switch($this->get()) {

            case "ua" : {

                $titles = array("хвилину","хвилини","хвилин");
                $titles2 = array("година","години","годин");
                $titles3 = array("день","дні","днів");
                $titles4 = array("місяць","місяці","місяців");
                $about = " тому";
                $now = "Тільки що";
                break;
            }

            case "ru" : {

                $titles = array("минуту","минуты","минут");
                $titles2 = array("час","часа","часов");
                $titles3 = array("день","дня","дней");
                $titles4 = array("месяц","месяца","місяців");
                $about = " назад";
                $now = "Только что";
                break;
            }

            default : {

                $titles = array("minute","minutes","minutes");
                $titles2 = array("hour","hours","hours");
                $titles3 = array("day","days","days");
                $titles4 = array("month","months","months");
                $about = " ago";
                $now = "less than a minute ago";

                break;
            }
        }

        $new_time = time();
        $time = $new_time - $time;

        if($time < 60) return $now; else
        if($time < 3600) return helper::declOfNum(($time-($time%60))/60, $titles).$about; else
        if($time < 86400) return helper::declOfNum(($time-($time%3600))/3600, $titles2).$about; else
        if($time < 2073600) return helper::declOfNum(($time - ($time % 86400)) / 86400, $titles3).$about; else
        if($time < 62208000) return helper::declOfNum(($time - ($time % 2073600)) / 2073600, $titles4).$about;
    }

    public function set($language)
    {
        $this->language = $language;
    }

    public function get()
    {
        return $this->language;
    }
}

