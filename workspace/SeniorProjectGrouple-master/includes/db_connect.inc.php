<?php

	//CONNECT TO THE DATABASE
	$DB_NAME = 'test';
	$DB_HOST = 'localhost';
	$DB_USER = 'root';
	$DB_PASS = 'Mo1Bi2Le3';

	$mysqli = new mysqli($DB_HOST, $DB_USER, $DB_PASS, $DB_NAME);

	if(mysqli_connect_errno())
	{
		printf("Connect failed: %s\n", mysqli_connect_error());
		exit();
	}

?>