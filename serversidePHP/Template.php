<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
