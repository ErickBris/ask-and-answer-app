<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

class questions extends db_connect
{

	private $requestFrom = 0;

	public function __construct($dbo = NULL)
    {
		parent::__construct($dbo);
	}

    public function count()
    {
        $stmt = $this->db->prepare("SELECT count(*) FROM qa WHERE toUserId = (:toUserId) AND replyAt = 0 AND removeAt = 0");
        $stmt->bindParam(":toUserId", $this->requestFrom, PDO::PARAM_INT);
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

    public function add_to_db($questionText, $language)
    {
        $currentTime = time();

        $stmt = $this->db->prepare("INSERT INTO qa_db (question, lang, createAt) value (:question, :lang, :createAt)");
        $stmt->bindParam(":question", $questionText, PDO::PARAM_STR);
        $stmt->bindParam(":lang", $language, PDO::PARAM_STR);
        $stmt->bindParam(":createAt", $currentTime, PDO::PARAM_INT);
        $stmt->execute();
    }

    public function add($questionText, $toUserId, $fromUserId = 0, $questionType = 0)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        if (strlen($questionText) == 0) {

            return $result;
        }

        $currentTime = time();
        $ip_addr = helper::ip_addr();
        $u_agent = helper::u_agent();

        $stmt = $this->db->prepare("INSERT INTO qa (toUserId, fromUserId, fromAccount, questionType, question, addedToListAt, createAt, ip_addr, u_agent) value (:toUserId, :fromUserId, :fromAccount, :questionType, :question, :addedToListAt, :createAt, :ip_addr, :u_agent)");
        $stmt->bindParam(":toUserId", $toUserId, PDO::PARAM_INT);
        $stmt->bindParam(":fromUserId", $fromUserId, PDO::PARAM_INT);
        $stmt->bindParam(":fromAccount", $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(":questionType", $questionType, PDO::PARAM_INT);
        $stmt->bindParam(":question", $questionText, PDO::PARAM_STR);
        $stmt->bindParam(":addedToListAt", $currentTime, PDO::PARAM_INT);
        $stmt->bindParam(":createAt", $currentTime, PDO::PARAM_INT);
        $stmt->bindParam(":ip_addr", $ip_addr, PDO::PARAM_STR);
        $stmt->bindParam(":u_agent", $u_agent, PDO::PARAM_STR);

        if ($this->requestFrom != $toUserId) {

            $gcm = new gcm($this->db, $toUserId);
            $gcm->setData(GCM_NOTIFY_QUESTION, "You have received new question", 0);
            $gcm->send();
        }

        if ($stmt->execute()) {

            $result = array("error" => false,
                            "error_code" => ERROR_SUCCESS,
                            "questionId" => $this->db->lastInsertId());
        }

        return $result;
    }

    public function remove($questionId)
    {
        $result = array("error" => true);

        $questionInfo = $this->info($questionId);

        if ($questionInfo['error'] === true) {

            return $result;
        }

        if ($questionInfo['toUserId'] != $this->requestFrom) {

            return $result;
        }

        if ($questionInfo['replyAt'] != 0) {

            return $result;
        }

        $currentTime = time();

        $stmt = $this->db->prepare("UPDATE qa SET removeAt = (:removeAt) WHERE id = (:questionId)");
        $stmt->bindParam(":questionId", $questionId, PDO::PARAM_INT);
        $stmt->bindParam(":removeAt", $currentTime, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $result = array("error" => false);
        }

        return $result;
    }

    public function delete($questionId)
    {
        $result = array("error" => true);

        $questionInfo = $this->info($questionId);

        if ($questionInfo['error'] === true) {

            return $result;
        }

        if ($questionInfo['replyAt'] != 0) {

            return $result;
        }

        $currentTime = time();

        $stmt = $this->db->prepare("UPDATE qa SET removeAt = (:removeAt) WHERE id = (:questionId)");
        $stmt->bindParam(":questionId", $questionId, PDO::PARAM_INT);
        $stmt->bindParam(":removeAt", $currentTime, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $result = array("error" => false);
        }

        return $result;
    }

    public function restore($questionId)
    {
        $result = array("error" => true);

        $questionInfo = $this->info($questionId);

        if ($questionInfo['error'] === true) {

            return $result;
        }

        $stmt = $this->db->prepare("UPDATE qa SET removeAt = 0 WHERE id = (:questionId)");
        $stmt->bindParam(":questionId", $questionId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $result = array("error" => false);
        }

        return $result;
    }

    public function edit($questionId, $questionText)
    {
        $result = array("error" => true);

        $questionInfo = $this->info($questionId);

        if ($questionInfo['error'] === true) {

            return $result;
        }

        $stmt = $this->db->prepare("UPDATE qa SET question = (:questionText) WHERE id = (:questionId)");
        $stmt->bindParam(":questionId", $questionId, PDO::PARAM_INT);
        $stmt->bindParam(":questionText", $questionText, PDO::PARAM_STR);

        if ($stmt->execute()) {

            $result = array("error" => false);
        }

        return $result;
    }

    public function reply($questionId, $answerText, $answerImg = '')
    {
        $result = array("error" => true);

        $questionInfo = $this->info($questionId);

        //this id not exists
        if ($questionInfo['error'] === true) {

            return $result;
        }

        if ($questionInfo['toUserId'] != $this->requestFrom) {

            return $result;
        }

        //this question has answer
        if ($questionInfo['replyAt'] != 0) {

            return $result;
        }

        //question has been removed
        if ($questionInfo['removeAt'] != 0) {

            return $result;
        }

        if (strlen($answerText) == 0 && strlen($answerImg) == 0) {

            return $result;
        }

        if (strlen($answerText) != 0) {

            $answerText = $answerText." ";
        }

        $currentTime = time();

        $stmt = $this->db->prepare("UPDATE qa SET answer = (:answerText), replyAt = (:replyAt), imgUrl = (:imgUrl), previewImgUrl = (:previewImgUrl) WHERE id = (:questionId)");
        $stmt->bindParam(":questionId", $questionId, PDO::PARAM_INT);
        $stmt->bindParam(":answerText", $answerText, PDO::PARAM_STR);
        $stmt->bindParam(":previewImgUrl", $answerImg, PDO::PARAM_STR);
        $stmt->bindParam(":imgUrl", $answerImg, PDO::PARAM_STR);
        $stmt->bindParam(":replyAt", $currentTime, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $result = array("error" => false);

            if ($this->requestFrom != $questionInfo['fromAccountId']) {

                $gcm = new gcm($this->db, $questionInfo['fromAccountId']);
                $gcm->setData(GCM_NOTIFY_ANSWER, "You have received new answer", $questionInfo['id']);
                $gcm->send();
            }

            //notify the sender reply
            $this->addReplyNotify($questionId);
        }

        return $result;
    }

    private function addReplyNotify($questionId)
    {
        $stmt = $this->db->prepare("SELECT * FROM qa WHERE id = (:questionId) LIMIT 1");
        $stmt->bindParam(":questionId", $questionId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                $row = $stmt->fetch();

                if ( ($row['fromAccount'] != 0) && ($row['fromAccount'] != $row['toUserId']) ) {

                    $currentTime = time();

                    $stmt2 = $this->db->prepare("INSERT INTO answers_notification (notifyToId, notifyFromId, answerId, createAt) value (:notifyToId, :notifyFromId, :answerId, :createAt)");
                    $stmt2->bindParam(":notifyToId", $row['fromAccount'], PDO::PARAM_INT);
                    $stmt2->bindParam(":notifyFromId", $row['toUserId'], PDO::PARAM_INT);
                    $stmt2->bindParam(":answerId", $row['id'], PDO::PARAM_INT);
                    $stmt2->bindParam(":createAt", $currentTime, PDO::PARAM_INT);
                    $stmt2->execute();
                }
            }
        }
    }

    public function info($questionId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("SELECT * FROM qa WHERE id = (:questionId) LIMIT 1");
        $stmt->bindParam(":questionId", $questionId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                $row = $stmt->fetch();

                $fromAccount = false;

                if ($row['fromAccount'] != 0) {

                    $fromAccount = true;
                }

                $fromUserFullname = "";

                if ($row['fromUserId'] != 0) {

                    $profile = new profile($this->db, $row['fromUserId']);
                    $fromUserFullname = $profile->getFullname();
                    unset($profile);
                }

                $result = array("error" => false,
                    "error_code" => ERROR_SUCCESS,
                    "id" => $row['id'],
                    "hash" => $row['hash'],
                    "toUserId" => $row['toUserId'],
                    "fromUserId" => $row['fromUserId'],
                    "fromUserFullname" => $fromUserFullname,
                    "fromAccount" => $fromAccount,
                    "fromAccountId" => $row['fromAccount'],
                    "questionType" => $row['questionType'],
                    "question" => htmlspecialchars_decode(stripslashes($row['question'])),
                    "addedToListAt" => $row['addedToListAt'],
                    "createAt" => $row['createAt'],
                    "replyAt" => $row['replyAt'],
                    "removeAt" => $row['removeAt']);
            }
        }

        return $result;
    }

    public function get($addedToListAt = 0)
    {
        if ($addedToListAt == 0) {

            $addedToListAt = time();
        }

        $questions = array("error" => false,
                           "error_code" => ERROR_SUCCESS,
                           "addedToListAt" => $addedToListAt,
                           "questions" => array());

        $stmt = $this->db->prepare("SELECT id FROM qa WHERE toUserId = (:toUserId) AND replyAt = 0 AND removeAt = 0 AND addedToListAt < (:addedToListAt) ORDER BY addedToListAt DESC LIMIT 20");
        $stmt->bindParam(':toUserId', $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(':addedToListAt', $addedToListAt, PDO::PARAM_INT);

        if ($stmt->execute()) {

            while ($row = $stmt->fetch()) {

                $questionInfo = $this->info($row['id']);

                array_push($questions['questions'], $questionInfo);

                $questions['addedToListAt'] = $questionInfo['addedToListAt'];

                unset($questionInfo);
            }
        }

        return $questions;
    }

    public function query($queryText = '', $addedToListAt = 0)
    {
        $originQuery = $queryText;

        if ($addedToListAt == 0) {

            $addedToListAt = time();
        }

        $questions = array("error" => false,
                        "error_code" => ERROR_SUCCESS,
                        "addedToListAt" => $addedToListAt,
                        "query" => $originQuery,
                        "questions" => array());

        $queryText = "%".$queryText."%";

        $stmt = $this->db->prepare("SELECT id FROM qa WHERE question LIKE (:query) AND replyAt = 0 AND removeAt = 0 AND addedToListAt < (:addedToListAt) ORDER BY addedToListAt DESC LIMIT 50");
        $stmt->bindParam(':query', $queryText, PDO::PARAM_STR);
        $stmt->bindParam(':addedToListAt', $addedToListAt, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $questionInfo = $this->info($row['id']);

                    array_push($questions['questions'], $questionInfo);

                    $questions['addedToListAt'] = $questionInfo['addedToListAt'];
                }
            }
        }

        return $questions;
    }

    public function setRequestFrom($requestFrom)
    {
        $this->requestFrom = $requestFrom;
    }

    public function getRequestFrom()
    {
        return $this->requestFrom;
    }

    private function search($userId, $questionText)
    {
        $stmt = $this->db->prepare("SELECT id FROM qa WHERE question = (:question) AND toUserId = (:toUserId) AND removeAt = 0 LIMIT 1");
        $stmt->bindParam(":toUserId", $userId, PDO::PARAM_INT);
        $stmt->bindParam(":question", $questionText, PDO::PARAM_STR);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {

            return true;
        }

        return false;
    }

    public function random($language)
    {
        $result = array("error" => true,
            "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("SELECT * FROM qa_db WHERE lang = (:language)");
        $stmt->bindParam(":language", $language, PDO::PARAM_STR);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {

            while ($row = $stmt->fetch()) {

                if (!$this->search($this->requestFrom, $row['question'])) {

                    $data = $this->add($row['question'], $this->requestFrom, 0, QUESTION_TYPE_RANDOM);

                    if ($data['error'] == false) {

                        $result = $this->info($data['questionId']);
                    }

                    break;
                }
            }
        }

        return $result;
    }
}
