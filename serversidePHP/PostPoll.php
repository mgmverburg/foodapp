<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

  //the group ID of the user creating the poll must be passed as parameter
  $GID = $_POST["GID"];

  require_once("DBConnect.php");
  include("GetGroupFunction.php");
  $groupResult = GetGroup($connection);
  if(mysqli_num_rows($groupResult) == 1) {
    $pollStatement = mysqli_prepare($connection, "INSERT INTO Poll (GID) VALUES (?)");
    mysqli_stmt_bind_param($pollStatement, "i", $GID);
    $pollStatementSuccess = mysqli_stmt_execute($pollStatement);

    //it can go wrong if GID is not a valid GID, since this is enforced in the database by a foreign key
    if ($pollStatementSuccess) {
      $response["isError"] = FALSE;
      $response["success_msg"] = "You successfully created a new poll";
    } else {
      $response["error_msg"] = "Only 1 poll can be active at a time";
    }
    mysqli_stmt_close($pollStatement);
  } else {
    $response["error_msg"] = "No group could be found with that ID";
  }



} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
