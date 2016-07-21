<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");
  include("GetGroupFunction.php");
  $groupResult = GetGroup($connection);
  if(mysqli_num_rows($groupResult) == 1) {
    $response = mysqli_fetch_assoc($groupResult); //this is if we want to respond with the user's info
    $GID = $_POST["GID"];
    $groupUserStatement = mysqli_prepare($connection, "SELECT username FROM User_Group WHERE GID = ?");
    mysqli_stmt_bind_param($groupUserStatement, "i", $GID);
    $groupUserStatementSuccess = mysqli_stmt_execute($groupUserStatement);
    $groupUserResult = mysqli_stmt_get_result($groupUserStatement);
    $response["users"] = mysqli_fetch_all($groupUserResult, $resulttype = MYSQLI_ASSOC);
    $response["isError"] = FALSE;
    $response["success_msg"] = "Successfully found the group with that ID";
    mysqli_stmt_close($groupUserStatement);
  } else {
    $response["error_msg"] = "No group could be found with that ID";
  }

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
