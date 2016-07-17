<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

  $username = $_POST["username"];
  $GID = $_POST["GID"];

  $userGroupStatement = mysqli_prepare($connection, "INSERT INTO User_Group (GID, username) VALUES (?, ?)");
  mysqli_stmt_bind_param($userGroupStatement, "ss", $GID, $username);
  $userGroupStatementSuccess = mysqli_stmt_execute($userGroupStatement);

  if ($userGroupStatementSuccess) {
    $response["isError"] = FALSE;
    $response["success_msg"] = "You successfully added " . $username . " to the group";
  } else {
    $response["error_msg"] = mysqli_error($connection);
  }
  mysqli_stmt_close($userGroupStatement);

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
