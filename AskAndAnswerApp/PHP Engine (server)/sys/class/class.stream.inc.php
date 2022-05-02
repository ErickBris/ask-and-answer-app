<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk, qascript@mail.ru
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

class stream extends db_connect
{
    private $requestFrom = 0;

    public function __construct($dbo = NULL)
    {
        parent::__construct($dbo);
    }

    public function get($replyAt = 0, $language = 'en')
    {
        if ($replyAt == 0) {

            $replyAt = time();
        }

        $answers = array("error" => false,
                         "error_code" => ERROR_SUCCESS,
                         "replyAt" => $replyAt,
                         "answers" => array());

        $stmt = $this->db->prepare("SELECT id FROM qa WHERE showInStream = 1 AND lang = (:lang) AND toUserId <> (:toId) AND replyAt > 0 AND replyAt < (:replyAt) ORDER BY replyAt DESC LIMIT 20");
        $stmt->bindParam(':toId', $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(':lang', $language, PDO::PARAM_STR);
        $stmt->bindParam(':replyAt', $replyAt, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $answer = new answers($this->db);
                    $answer->setRequestFrom($this->requestFrom);
                    $answerInfo = $answer->info($row['id']);
                    unset($answer);

                    array_push($answers['answers'], $answerInfo);

                    $answers['replyAt'] = $answerInfo['replyAt'];

                    unset($answerInfo);
                }
            }
        }

        return $answers;
    }

    public function setRequestFrom($requestFrom)
    {
        $this->requestFrom = $requestFrom;
    }

    public function getRequestFrom()
    {
        return $this->requestFrom;
    }
}

