<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once("DBConnect.php");

  $username = $_POST["username"];
  // $password = $_POST["password"];
  $passwordHash = sha1($_POST['password']);

  //checks if credentials exists
  $credentialsStatement = mysqli_prepare($connection, "SELECT * FROM User WHERE username = ? AND password = ?");
  mysqli_stmt_bind_param($credentialsStatement, "ss", $username, $passwordHash);
  $credentialsStatementSuccess = mysqli_stmt_execute($credentialsStatement);
  $credentialsResult = mysqli_stmt_get_result($credentialsStatement);

  // If result matched $username and $password, there is exactly 1 resulting table row
  if(mysqli_num_rows($credentialsResult) == 1) {
    $assocArray = mysqli_fetch_assoc($credentialsResult); //this is if we want to respond with the user's info
    $response["username"] = $assocArray["username"];
    $response["isError"] = FALSE;
    $response["success_msg"] = "You successfully logged in";
  } else {
    $response["error_msg"] = "Your login name or password is incorrect";
  }
  mysqli_stmt_close($credentialsStatement);
} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
