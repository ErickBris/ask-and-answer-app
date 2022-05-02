<?php

/*!
     * QA Script v2.0
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * qascript@ifsoft.co.uk
     *
     * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
     */

class update extends db_connect
{
	private $requestFrom = 0;

	public function __construct($dbo = NULL)
    {
		parent::__construct($dbo);

        $stmt = $this->db->prepare("ALTER TABLE users ADD gcm_regid TEXT NOT NULL after id");
        $stmt->execute();
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
