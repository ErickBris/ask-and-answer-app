<?php

/*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

class feed extends db_connect
{
	private $requestFrom = 0;

	public function __construct($dbo = NULL)
    {
		parent::__construct($dbo);
	}

    public function get($replyAt = 0)
    {
        if ($replyAt == 0) {

            $replyAt = time();
        }

        $feed = array("error" => false,
                      "error_code" => ERROR_SUCCESS,
                      "replyAt" => $replyAt,
                      "answers" => array());

        $stmt = $this->db->prepare("SELECT * FROM profile_followers WHERE follower = (:followerId) AND create_at < (:replyAt) ORDER BY create_at DESC");
        $stmt->bindParam(':followerId', $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(':replyAt', $replyAt, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $answers = array();

            while ($row = $stmt->fetch()) {

                $stmt2 = $this->db->prepare("SELECT id, replyAt FROM qa WHERE toUserId = (:toUserId) AND replyAt < (:replyAt) AND replyAt > (:followAt) ORDER BY replyAt DESC");
                $stmt2->bindParam(':toUserId', $row['follow_to'], PDO::PARAM_INT);
                $stmt2->bindParam(':replyAt', $replyAt, PDO::PARAM_INT);
                $stmt2->bindParam(':followAt', $row['create_at'], PDO::PARAM_INT);
                $stmt2->execute();

                while ($row2 = $stmt2->fetch())  {

                    $answers[] = array("replyAt" => $row2['replyAt'], "answerId" => $row2['id']);
                }
            }

            $currentItem = 0;
            $maxItem = 20;

            if ($answersCount = count($answers)) {

                arsort($answers);

                foreach ($answers as $key => $value) {

                    if ($currentItem < $maxItem) {

                        $currentItem++;

                        $answer = new answers($this->db);
                        $answer->setRequestFrom($this->requestFrom);

                        $answerInfo = $answer->info($value['answerId']);
                        unset($answer);

                        array_push($feed['answers'], $answerInfo);

                        $feed['replyAt'] = $answerInfo['replyAt'];

                        unset($answerInfo);
                        unset($answer);
                    }
                }
            }
        }

        return $feed;
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
