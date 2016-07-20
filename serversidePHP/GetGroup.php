<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");
  include("GetGroupFunction.php");
  $groupResult = GetGroup($connection);
  if(mysqli_num_rows($groupResult) == 1) {
    $response = mysqli_fetch_assoc($groupResult); //this is if we want to respond with the user's info
    $response["isError"] = FALSE;
    $response["success_msg"] = "Successfully found the group with that ID";
  } else {
    $response["error_msg"] = "No group could be found with that ID";
  }

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
