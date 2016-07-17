<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

  $GID = $_POST["GID"];

  $groupStatement = mysqli_prepare($connection, "SELECT * FROM Group WHERE GID = ?");
  mysqli_stmt_bind_param($groupStatement, "i", $GID);
  $groupStatementSuccess = mysqli_stmt_execute($groupStatement);
  $groupResult = mysqli_stmt_get_result($groupStatement);

  if(mysqli_num_rows($groupResult) == 1) {
    $response = mysqli_fetch_assoc($groupResult); //this is if we want to respond with the user's info
    $response["isError"] = FALSE;
    $response["success_msg"] = "Successfully found the group with that ID";
  } else {
    $response["error_msg"] = "No group could be found with that ID";
  }
  mysqli_stmt_close($groupStatement);

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
