<?php

/*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

class friends extends db_connect {

    private $requestFrom = 0;

	public function __construct($dbo = NULL)
    {
		parent::__construct($dbo);
	}

	public function get($createAt = 0)
    {
        if ($createAt == 0) {

            // Current time
            $createAt = time();
        }

		$friends = array("error" => false,
                         "error_code" => ERROR_SUCCESS,
                         "createAt" => $createAt,
                         "friends" => array());

		$stmt = $this->db->prepare("SELECT follow_to, create_at FROM profile_followers WHERE follower = (:follower) AND create_at < (:create_at) ORDER BY create_at DESC LIMIT 20");
        $stmt->bindParam(':follower', $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(':create_at', $createAt, PDO::PARAM_INT);

		if ($stmt->execute()) {

			if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $profile = new profile($this->db, $row['follow_to']);
                    $profile->setRequestFrom($this->requestFrom);

                    array_push($friends['friends'], $profile->get());

                    $friends['createAt'] = $row['create_at'];

                    unset($profile);
                }
			}
		}

        return $friends;
	}

    public function setRequestFrom($request_from)
    {
        $this->requestFrom = $request_from;
    }

    public function getRequestFrom()
    {
        return $this->requestFrom;
    }
}
