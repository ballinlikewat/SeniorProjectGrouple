<?php

	include_once('/../includes/db_connect.inc.php');

	$stmt = $mysqli->prepare("UPDATE friends SET rec_date = CURRENT_TIMESTAMP where sender = ? AND receiver = ?");
	if($stmt === false)
	{
		echo "error in sql";
	}
	$stmt->bind_param('ss', $_POST['sender'], $_POST['receiver']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$result["success"] = 1;
			$result["message"] = "Friend request accepted!";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 0;
			$result["message"] = "Friend request was not found!";
			echo json_encode ( $result );
		}

	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Failed to write to database";
		echo json_encode ( $result );
	}		
	

	$mysqli->close();
	
?>