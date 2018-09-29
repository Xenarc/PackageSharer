<?php
$servername = "localhost";
$username = "root";
$password = "root";
$dbname = "PackageShare";

if(isset( $_POST["ID"]) && isset( $_POST["Lng"]) && isset( $_POST["Lat"] && isset( $_POST["Raduis"])) {
	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	}
	$ID = strip_tags(trim($_POST["ID"]));
	$Lng = strip_tags(trim($_POST["Lng"]));
	$Lat = strip_tags(trim($_POST["Lat"]));
	$Radius = strip_tags(trim($_POST["Radius"]));
	
	$sql ="SELECT * , (
	       3959 * acos (
	       cos ( radians($Lat) )
	       * cos( radians( Lat ) )
	       * cos( radians( Lng ) - radians($Lng) )
	       + sin ( radians($Lat) )
	       * sin( radians( Lat ) )
	     )) AS distance
	 		FROM Drivers
			HAVING distance < $Radius";
	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
	$rows = array();
	    while($row = $result->fetch_assoc()) {
	        // echo "ID: " . $row["ID"];
		    $rows[] = $row;
		}
		echo json_encode($rows);
	} else {
	    echo "0 results";
	}
	$conn->close();
}

// if(isset( $_POST["ID"]) && isset( $_POST["Lng"]) && isset( $_POST["Lat"])) {
// 	$conn = new mysqli($servername, $username, $password, $dbname);
// 	if($conn == false) {
// 		die("Connection Error: " . mysqli_error_connect());
// 	}
//
// 	$ID = strip_tags(trim($_POST["ID"]));
// 	$Lng = strip_tags(trim($_POST["Lng"]));
// 	$Lat = strip_tags(trim($_POST["Lat"]));
// 	$sql = "SELECT * FROM Drivers;";
//
// // 	$sql ="SELECT
// //     id, (
// //       3959 * acos (
// //       cos ( radians($Lat) )
// //       * cos( radians( lat ) )
// //       * cos( radians( lng ) - radians($Lng) )
// //       + sin ( radians($Lat) )
// //       * sin( radians( lat ) )
// //     )
// // ) AS distance
// // FROM markers
// // HAVING distance < 30
// // ORDER BY distance
// // LIMIT 0 , 20";
//
// 	$result mysqli_query($connection, $sql) or die(mysqli_error($connection));
// 	echo $conn;
//
//
// 	// Close DB connection
// 	$conn->close();
// }
// // POST Vars Not Given.
// ?>