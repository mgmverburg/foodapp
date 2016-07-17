<?php
  $response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once('DBConnect.php');

  $username = $_POST["username"];
  $password = $_POST["password"];

  //checks if username exists
  $existingUsernameStatement = mysqli_prepare($connection, "SELECT Username FROM User WHERE username = ?");
  mysqli_stmt_bind_param($existingUsernameStatement, "s", $username);
  //@TODO: do I need double check for failure? Both for execute and get_result?
  $existingUsernameStatementSuccess = mysqli_stmt_execute($existingUsernameStatement);
  $existingUsernameResult = mysqli_stmt_get_result($existingUsernameStatement);

  if(mysqli_num_rows($existingUsernameResult) == 0) { //if no results with that username, then username available
    mysqli_stmt_close($existingUsernameStatement);
    //The question marks tell the prepare statement that it needs to pass a parameter to it
    $userStatement = mysqli_prepare($connection, "INSERT INTO User (username, password) VALUES (?, ?)");
    mysqli_stmt_bind_param($userStatement, "ss", $username, $password);
    $userStatementSuccess = mysqli_stmt_execute($userStatement);

    if ($userStatementSuccess) {
      $response["isError"] = FALSE;
      $response["success_msg"] = "You successfully registered";
    } else {
      $response["error_msg"] = mysqli_error($connection);
    }
    mysqli_stmt_close($userStatement);
  } else {
    $response["error_msg"] = "That username is already in use";
  }

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
