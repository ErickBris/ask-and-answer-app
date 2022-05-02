<?php

/*!
 * QA Script v2.0
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * qascript@ifsoft.co.uk, qascript@mail.ru
 *
 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
 */

class hashtag extends db_connect {

    private $requestFrom = 0;
    private $language = 'en';

	public function __construct($dbo = NULL)
	{
		parent::__construct($dbo);
	}

    public function answersCount()
    {
        $stmt = $this->db->prepare("SELECT max(id) FROM qa");
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

	public function count($hashtag)
	{
        $hashtag = str_replace('#', '', $hashtag);
		$search_explode = explode(' ',trim($hashtag, ' '));

		$sql = "SELECT count(*) FROM qa WHERE (answer LIKE '%#{$search_explode[0]} %' OR answer LIKE '#{$search_explode[0]}' OR answer LIKE '% #{$search_explode[0]} %') AND removeAt = 0 AND replyAt <> 0";

		$stmt = $this->db->prepare($sql);
		$stmt->execute();

		return $number_of_rows = $stmt->fetchColumn();
	}

	public function search($hashtag, $answerId = 0)
    {
        $originQuery = $hashtag;

        if ($answerId == 0) {

            $answerId = $this->answersCount();
            $answerId++;
        }

        $answers = array("error" => false,
                         "error_code" => ERROR_SUCCESS,
                         "answerId" => $answerId,
                         "query" => $originQuery,
                         "answers" => array());

        $hashtag = str_replace('#', '', $hashtag);
        $search_explode = explode(' ', trim($hashtag, ' '));

        $sql = "SELECT id FROM qa WHERE (answer LIKE '%#{$search_explode[0]} %' OR answer LIKE '#{$search_explode[0]}' OR answer LIKE '% #{$search_explode[0]} %') AND removeAt = 0 AND replyAt <> 0 AND id < (:answerId) ORDER BY id DESC LIMIT 20";

        $stmt = $this->db->prepare($sql);
        $stmt->bindParam(':answerId', $answerId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    $answer = new answers($this->db);
                    $answer->setRequestFrom($this->getRequestFrom());

                    $answerInfo = $answer->info($row['id']);

                    array_push($answers['answers'], $answerInfo);

                    $answers['answerId'] = $answerInfo['id'];

                    unset($answer);
                    unset($answerInfo);
                }
            }
        }

        return $answers;
    }

    public function setLanguage($language)
    {
        $this->language = $language;
    }

    public function getLanguage()
    {
        return $this->language;
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