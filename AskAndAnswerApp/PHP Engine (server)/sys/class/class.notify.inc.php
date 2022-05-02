<?php

    /*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk, qascript@mail.ru
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

class notify extends db_connect
{

    private $requestFrom = 0;

    public function __construct($dbo = NULL)
    {

        parent::__construct($dbo);
    }

    public function getLikes($createAt = 0)
    {

        if ($createAt == 0) {

            $createAt = time();
        }

        $likes = array("error" => false,
                       "error_code" => ERROR_SUCCESS,
                       "createAt" => $createAt,
                       "likes" => array());

        $stmt = $this->db->prepare("SELECT * FROM likes_notification WHERE notifyToId = (:notifyToId) AND createAt < (:createAt) ORDER BY createAt DESC LIMIT 20");
        $stmt->bindParam(':notifyToId', $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(':createAt', $createAt, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $profile = new profile($this->db, $row['notifyFromId']);
                    $profileInfo = $profile->get();
                    unset($profile);

                    $data = array("id" => $row['id'],                                   //notify id
                                  "type" => NOTIFY_TYPE_LIKE,
                                  "answerId" => $row['answerId'],
                                  "fromUserId" => $profileInfo['id'],
                                  "fromUserState" => $profileInfo['state'],
                                  "fromUserUsername" => $profileInfo['username'],
                                  "fromUserFullname" => $profileInfo['fullname'],
                                  "fromUserPhotoUrl" => $profileInfo['lowPhotoUrl'],
                                  "createAt" => $row['createAt']);                      //notify created at (timeshtamp)

                    array_push($likes['likes'], $data);

                    $likes['createAt'] = $row['createAt'];

                    unset($data);
                }
            }
        }

        return $likes;
    }

    public function getAnswers($createAt = 0)
    {

        if ($createAt == 0) {

            $createAt = time();
        }

        $answers = array("error" => false,
                         "error_code" => ERROR_SUCCESS,
                         "createAt" => $createAt,
                         "answers" => array());

        $stmt = $this->db->prepare("SELECT * FROM answers_notification WHERE notifyToId = (:notifyToId) AND createAt < (:createAt) ORDER BY createAt DESC LIMIT 20");
        $stmt->bindParam(':notifyToId', $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(':createAt', $createAt, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $profile = new profile($this->db, $row['notifyFromId']);
                    $profileInfo = $profile->get();
                    unset($profile);

                    $data = array("id" => $row['id'],
                                  "type" => NOTIFY_TYPE_ANSWER,
                                  "answerId" => $row['answerId'],
                                  "fromUserId" => $profileInfo['id'],
                                  "fromUserState" => $profileInfo['state'],
                                  "fromUserUsername" => $profileInfo['username'],
                                  "fromUserFullname" => $profileInfo['fullname'],
                                  "fromUserPhotoUrl" => $profileInfo['lowPhotoUrl'],
                                  "createAt" => $row['createAt']);

                    array_push($answers['answers'], $data);

                    $answers['createAt'] = $row['createAt'];

                    unset($data);
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
