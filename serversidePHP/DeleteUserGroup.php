<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");
  $username = $_POST["username"];
  $GID = $_POST["GID"];
  include("GetUserGroupFunction.php");
  $GIDResult = GetUserGroup($connection);

  include("GetGroupFunction.php");
  $groupResult = GetGroup($connection);
  if(mysqli_num_rows($groupResult) == 1) {
    if(mysqli_num_rows($GIDResult) == 1) {
      $userGroupStatement = mysqli_prepare($connection, "DELETE FROM User_Group WHERE GID = ? AND username = ? ");
      mysqli_stmt_bind_param($userGroupStatement, "is", $GID, $username);
      $userGroupStatementSuccess = mysqli_stmt_execute($userGroupStatement);

      if ($userGroupStatementSuccess) {
        $response["isError"] = FALSE;
        $assocArray = mysqli_fetch_assoc($groupResult); //this is if we want to respond with the user's info
        $response["success_msg"] = 'You successfully removed ' . $username . ' from "' . $assocArray["name"] . '"';
      } else {
        $response["error_msg"] = mysqli_error($connection);
      }
      mysqli_stmt_close($userGroupStatement);
    } else {
      $response["error_msg"] = "The user you are trying to remove from the group is not part of the group or was already removed";
    }
  } else {
    $response["error_msg"] = "The group you are trying to remove the user from does not exist";
  }

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
