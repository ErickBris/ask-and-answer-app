<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

class search extends db_connect
{
	public function __construct($dbo = NULL)
    {
		parent::__construct($dbo);
	}

    private function getCount($queryText)
    {
        $queryText = "%".$queryText."%";

        $stmt = $this->db->prepare("SELECT count(*) FROM users WHERE state = 0 AND login LIKE (:query)");
        $stmt->bindParam(':query', $queryText, PDO::PARAM_STR);
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

	public function query($accountId, $queryText = '', $createAt = 0)
    {
        $originQuery = $queryText;

		if ($createAt == 0) {

            $createAt = time();
        }

		$users = array("error" => false,
                       "error_code" => ERROR_SUCCESS,
                       "itemCount" => $this->getCount($originQuery),
					   "createAt" => $createAt,
					   "query" => $originQuery,
                       "users" => array());

        $queryText = "%".$queryText."%";

		$stmt = $this->db->prepare("SELECT id, regtime FROM users WHERE state = 0 AND login LIKE (:query) AND regtime < (:createAt) ORDER BY regtime DESC LIMIT 20");
		$stmt->bindParam(':query', $queryText, PDO::PARAM_STR);
        $stmt->bindParam(':createAt', $createAt, PDO::PARAM_STR);

		if ($stmt->execute()) {

			if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $profile = new profile($this->db, $row['id']);
                    $profile->setRequestFrom($accountId);

                    array_push($users['users'], $profile->get());

                    $users['createAt'] = $row['regtime'];

                    unset($profile);
                }
			}
		}

        return $users;
	}
}

