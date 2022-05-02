<?php

/*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

class wall extends db_connect
{
    private $requestFrom = 0;
    private $profileId;

    public function __construct($dbo = NULL, $profileId = 0)
    {
        parent::__construct($dbo);

        $this->setProfileId($profileId);
    }

    public function get($replyAt = 0)
    {
        if ($replyAt == 0) {

            $replyAt = time();
        }

        $answers = array("error" => false,
                         "error_code" => ERROR_SUCCESS,
                         "replyAt" => $replyAt,
                         "answers" => array());

        $stmt = $this->db->prepare("SELECT * FROM qa WHERE toUserId = (:toUserId) AND replyAt > 0 AND replyAt < (:replyAt) ORDER BY replyAt DESC LIMIT 20");
        $stmt->bindParam(':toUserId', $this->profileId, PDO::PARAM_INT);
        $stmt->bindParam(':replyAt', $replyAt, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $wallItem = new answers($this->db, $this->profileId);
                    $wallItem->setRequestFrom($this->requestFrom);

                    $answerInfo = $wallItem->info($row['id']);

                    array_push($answers['answers'], $answerInfo);

                    $answers['replyAt'] = $answerInfo['replyAt'];

                    unset($wallItem);
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

    public function setProfileId($profileId)
    {
        $this->profileId = $profileId;
    }

    public function getProfileId()
    {
        return $this->profileId;
    }
}

