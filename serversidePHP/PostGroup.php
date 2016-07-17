<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once('DBConnect.php');

  $username = $_POST["username"];
  $groupname = $_POST["groupname"];

	// $groupStatement = mysqli_prepare($connection, "INSERT INTO User (username, password) VALUES (?, ?)");
  $groupStatement = mysqli_prepare($connection, "INSERT INTO Group (GID, name, admin) VALUES (DEFAULT, 'test', 'test')");
  // mysqli_report(MYSQLI_REPORT_ALL);
//   if ($groupStatement == FALSE) {
//     die ("Mysql Error: " . mysqli_error($connection));
// }
  mysqli_stmt_bind_param($groupStatement, "ss", $groupname, $username);
  $groupStatementSuccess = mysqli_stmt_execute($groupStatement);

  //it can go wrong if the username is not valid, because of the foreign key constraint
  //or if the groupname is too long
  if ($groupStatementSuccess) {
    $response["isError"] = FALSE;
    $response["success_msg"] = "You successfully created a new group";
  } else {
    $response["error_msg"] = mysqli_error($connection);
  }
  mysqli_stmt_close($groupStatement);

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
