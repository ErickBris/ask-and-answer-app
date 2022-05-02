<?php

/*!
	 * QA Script v2.0
	 *
	 * http://ifsoft.com.ua, http://ifsoft.co.uk
	 * qascript@ifsoft.co.uk, qascript@mail.ru
	 *
	 * Copyright 2012-2015 Demyanchuk Dmitry (https://vk.com/dmitry.demyanchuk)
	 */

session_start();

include_once("../sys/core/init.inc.php");

$page_id = '';

error_reporting(E_ALL);

if (!empty($_GET)) {

    if (!isset($_GET['q'])) {

        include_once("../html/main.inc.php");
        exit;
    }

    $request = htmlentities($_GET['q'], ENT_QUOTES);
    $request = helper::escapeText($request);
    $request = explode('/', trim($request, '/'));

    $cnt = count($request);

	switch ($cnt) {

		case 0: {

			include_once("../html/main.inc.php");
			exit;
		}

		case 1: {

			if (file_exists("../html/page.".$request[0].".inc.php")) {

				include_once("../html/page.".$request[0].".inc.php");
				exit;

			}  else if ($helper->isLoginExists($request[0])) {

				include_once("../html/profile.inc.php");
				exit;

			} else {

				include_once("../html/error.inc.php");
				exit;
			}
		}

		case 2: {

			if (file_exists( "../html/".$request[0]."/page.".$request[1].".inc.php") ) {

				include_once("../html/" . $request[0] . "/page." . $request[1] . ".inc.php");
				exit;

			} else {

				include_once("../html/error.inc.php");
				exit;
			}
		}


		case 4: {

            switch ($request[0]) {

                case 'api': {

                    if (file_exists("../api/".$request[1]."/method/".$request[3].".inc.php")) {

                        include_once("../sys/config/api.inc.php");

                        include_once("../api/".$request[1]."/method/".$request[3].".inc.php");
                        exit;

                    } else {

                        include_once("../html/error.inc.php");
                        exit;
                    }

                    break;
                }

                default: {

                    include_once("../html/error.inc.php");
                    exit;
                }
            }
		}

		default: {

			include_once("../html/error.inc.php");
			exit;
		}
	}
} else {

	$request = array();
	include_once("../html/main.inc.php");
	exit;
}

?>