<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

class answers extends db_connect
{

	private $requestFrom = 0;
    private $profileId;

	public function __construct($dbo = NULL, $profileId = 0)
    {
		parent::__construct($dbo);

        $this->setProfileId($profileId);
	}

    public function count()
    {
        $stmt = $this->db->prepare("SELECT count(*) FROM qa WHERE toUserId = (:toUserId) AND replyAt <> 0 AND removeAt = 0");
        $stmt->bindParam(":toUserId", $this->profileId, PDO::PARAM_INT);
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

    public function remove($answerId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $answerInfo = $this->info($answerId);

        if ($answerInfo['error'] === true) {

            return $result;
        }

        if ($answerInfo['toUserId'] != $this->requestFrom) {

            return $result;
        }

        if ($answerInfo['replyAt'] == 0) {

            return $result;
        }

        $currentTime = time();

        $stmt = $this->db->prepare("UPDATE qa SET addedToListAt = (:addedToListAt), answer = '', likesCount = 0, replyAt = 0, previewImgUrl = '', imgUrl = ''  WHERE id = (:answerId)");
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);
        $stmt->bindParam(":addedToListAt", $currentTime, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $this->removeReplyNotify($answerId);
            $this->removeLikesNotify($answerId);
            $this->removeLikes($answerId);

            $profile = new profile($this->db, $this->requestFrom);

            $result = array("error" => false,
                            "error_code" => ERROR_SUCCESS,
                            "likesCount" => $profile->getLikesCount(),
                            "answersCount" => $profile->getAnswersCount());

            unset($profile);
        }

        return $result;
    }

    public function like($answerId, $fromUserId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $answerInfo = $this->info($answerId);

        if ($answerInfo['error'] === true) {

            return $result;
        }

        if ($answerInfo['replyAt'] == 0) {

            return $result;
        }

        if ($this->is_like_exists($answerId, $fromUserId)) {


            $this->removeLikeNotify($answerId, $fromUserId);
            $this->removeLike($answerId, $fromUserId);

            $myLike = false;

        } else {

            $createAt = time();
            $ip_addr = helper::ip_addr();

            $stmt = $this->db->prepare("INSERT INTO likes (toUserId, fromUserId, answerId, createAt, ip_addr) value (:toUserId, :fromUserId, :answerId, :createAt, :ip_addr)");
            $stmt->bindParam(":toUserId", $answerInfo['toUserId'], PDO::PARAM_INT);
            $stmt->bindParam(":fromUserId", $fromUserId, PDO::PARAM_INT);
            $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);
            $stmt->bindParam(":createAt", $createAt, PDO::PARAM_INT);
            $stmt->bindParam(":ip_addr", $ip_addr, PDO::PARAM_STR);
            $stmt->execute();

            if ($answerInfo['toUserId'] != $fromUserId) {

                $stmt2 = $this->db->prepare("INSERT INTO likes_notification (notifyToId, notifyFromId, answerId, createAt) value (:notifyToId, :notifyFromId, :answerId, :createAt)");
                $stmt2->bindParam(":notifyToId", $answerInfo['toUserId'], PDO::PARAM_INT);
                $stmt2->bindParam(":notifyFromId", $fromUserId, PDO::PARAM_INT);
                $stmt2->bindParam(":answerId", $answerId, PDO::PARAM_INT);
                $stmt2->bindParam(":createAt", $createAt, PDO::PARAM_INT);
                $stmt2->execute();

                $gcm = new gcm($this->db, $answerInfo['toUserId']);
                $gcm->setData(GCM_NOTIFY_LIKE, "You have new like", $answerId);
                $gcm->send();
            }

            $myLike = true;
        }

        $likesCount = $this->updateLikesCount($answerId);

        $result = array("error" => false,
                        "error_code" => ERROR_SUCCESS,
                        "likesCount" => $likesCount,
                        "myLike" => $myLike);

        return $result;
    }

    private function is_like_exists($answerId, $fromUserId)
    {
        $stmt = $this->db->prepare("SELECT id FROM likes WHERE fromUserId = (:fromUserId) AND answerId = (:answerId) LIMIT 1");
        $stmt->bindParam(":fromUserId", $fromUserId, PDO::PARAM_INT);
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {

            return true;
        }

        return false;
    }

    private function removeReplyNotify($answerId)
    {
        $stmt = $this->db->prepare("DELETE FROM answers_notification WHERE answerId = (:answerId)");
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            return true;
        }

        return false;
    }

    private function removeLikeNotify($answerId, $notifyFromId)
    {
        $stmt = $this->db->prepare("DELETE FROM likes_notification WHERE notifyFromId = (:notifyFromId) AND answerId = (:answerId)");
        $stmt->bindParam(":notifyFromId", $notifyFromId, PDO::PARAM_INT);
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            return true;
        }

        return false;
    }

    private function removeLikesNotify($answerId)
    {
        $stmt = $this->db->prepare("DELETE FROM likes_notification WHERE answerId = (:answerId)");
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            return true;
        }

        return false;
    }

    private function getLikesCount($answerId)
    {
        $stmt = $this->db->prepare("SELECT count(*) FROM likes WHERE answerId = (:answerId)");
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

    private function updateLikesCount($answerId)
    {
        $likesCount = $this->getLikesCount($answerId);

        $stmt = $this->db->prepare("UPDATE qa SET likesCount = (:likesCount) WHERE id = (:answerId)");
        $stmt->bindParam(":likesCount", $likesCount, PDO::PARAM_INT);
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);
        $stmt->execute();

        return $likesCount;
    }

    private function removeLikes($answerId)
    {
        $stmt = $this->db->prepare("DELETE FROM likes WHERE answerId = (:answerId)");
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            return true;
        }

        return false;
    }

    private function removeLike($answerId, $fromUserId)
    {
        $stmt = $this->db->prepare("DELETE FROM likes WHERE answerId = (:answerId) AND fromUserId = (:fromUserId)");
        $stmt->bindParam(":fromUserId", $fromUserId, PDO::PARAM_INT);
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            return true;
        }

        return false;
    }

    public function info($answerId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("SELECT * FROM qa WHERE id = (:answerId) LIMIT 1");
        $stmt->bindParam(":answerId", $answerId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                $row = $stmt->fetch();

                $myLike = false;

                if ($this->requestFrom != 0) {

                    if ($this->is_like_exists($answerId, $this->requestFrom)) {

                        $myLike = true;
                    }
                }

                $fromUserId = array("fullname" => '',
                                    "username" => '');

                if ($row['fromUserId'] != 0) {

                    $profile = new profile($this->db, $row['fromUserId']);
                    $fromUserId = $profile->get();
                    unset($profile);
                }

                $profile = new profile($this->db, $row['toUserId']);
                $toUserId = $profile->get();
                unset($profile);

                $result = array("error" => false,
                                "error_code" => ERROR_SUCCESS,
                                "id" => $row['id'],
                                "toUserId" => $row['toUserId'],
                                "toUserState" => $toUserId['state'],
                                "toUserUsername" => $toUserId['username'],
                                "toUserFullname" => $toUserId['fullname'],
                                "toUserPhotoUrl" => $toUserId['lowPhotoUrl'],
                                "fromUserId" => $row['fromUserId'],
                                "fromUserUsername" => $fromUserId['username'],
                                "fromUserFullname" => $fromUserId['fullname'],
                                "questionType" => $row['questionType'],
                                "question" => htmlspecialchars_decode(stripslashes($row['question'])),
                                "answer" => htmlspecialchars_decode(stripslashes($row['answer'])),
                                "likesCount" => $row['likesCount'],
                                "myLike" => $myLike,
                                "lang" => $row['lang'],
                                "replyAt" => $row['replyAt'],
                                "previewImgUrl" => $row['previewImgUrl'],
                                "imgUrl" => $row['imgUrl'],
                                "previewGifUrl" => $row['previewGifUrl'],
                                "gifUrl" => $row['gifUrl'],
                                "previewVideoImgUrl" => $row['previewVideoImgUrl'],
                                "videoUrl" => $row['videoUrl']);
            }
        }

        return $result;
    }

    public function get($answerId = 0)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN,
                        "answerId" => $answerId,
                        "answer" => array());

        $data = $this->info($answerId);

        if ($data['error'] === true) {

            return $result;
        }

        if ($data['replyAt'] == 0) {

            return $result;
        }

        $result['error'] = false;
        $result['error_code'] = ERROR_SUCCESS;
        array_push($result['answer'], $data);

        return $result;
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

