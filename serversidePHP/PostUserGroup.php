<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

  $username = $_POST["username"];
  $GID = $_POST["GID"];

  include("GetGroupFunction.php");
  $groupResult = GetGroup($connection);
  if(mysqli_num_rows($groupResult) == 1) {
    include("GetUserFunction.php");
    $userResult = GetUser($connection);
    if(mysqli_num_rows($userResult) == 1) {
      $userGroupStatement = mysqli_prepare($connection, "INSERT INTO User_Group (GID, username) VALUES (?, ?)");
      mysqli_stmt_bind_param($userGroupStatement, "ss", $GID, $username);
      $userGroupStatementSuccess = mysqli_stmt_execute($userGroupStatement);

      if ($userGroupStatementSuccess) {
        $response["isError"] = FALSE;
        $assocArray = mysqli_fetch_assoc($groupResult);
        $response["success_msg"] = "You successfully added " . $username . ' to the group "' . $assocArray["name"] . '"';
      } else {
        //@TODO: this changes if multiple group membership is possible
        $response["error_msg"] = "The user " . $username . " is already part of a group";
      }
    } else {
      $response["error_msg"] = "The user " . $username . " does not exist";
    }
  } else {
    $response["error_msg"] = "Invalid group";
  }

  mysqli_stmt_close($userGroupStatement);

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
