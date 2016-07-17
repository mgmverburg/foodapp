<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

  //the group ID of the user creating the poll must be passed as parameter
  $GID = $_POST["GID"];

  $pollStatement = mysqli_prepare($connection, "INSERT INTO Poll (GID) VALUES (?)");
  mysqli_stmt_bind_param($pollStatement, "i", $GID);
  $pollStatementSuccess = mysqli_stmt_execute($pollStatement);

  //it can go wrong if GID is not a valid GID, since this is enforced in the database by a foreign key
  if ($pollStatementSuccess) {
    $response["isError"] = FALSE;
    $response["success_msg"] = "You successfully created a new poll";
  } else {
    $response["error_msg"] = "Impossible to create a poll for an unexisting group";
  }
  mysqli_stmt_close($pollStatement);

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
