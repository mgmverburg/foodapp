<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

  $username = $_POST["username"];

  $GIDStatement = mysqli_prepare($connection, "SELECT * FROM User_Group WHERE username = ?");
  mysqli_stmt_bind_param($GIDStatement, "s", $username);
  $GIDStatementSuccess = mysqli_stmt_execute($GIDStatement);
  $GIDResult = mysqli_stmt_get_result($credentialsStatement);

  if(mysqli_num_rows($GIDResult) == 1) {
    $assocArray = mysqli_fetch_assoc($GIDResult); //this is if we want to respond with the user's info
    $response["GID"] = $assocArray["GID"];
    $response["isError"] = FALSE;
    $response["success_msg"] = "Successfully found the ID of the group the user belongs to";
  } else {
    $response["error_msg"] = "No group could be found for this user";
  }
  mysqli_stmt_close($GIDStatement);

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
