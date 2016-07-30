<?php
$response["isError"] = TRUE;
if($_SERVER["REQUEST_METHOD"]=="POST"){
  require_once('DBConnect.php');

  $username = $_POST["admin"];
  $groupname = $_POST["name"];

  $groupStatement = mysqli_prepare($connection, "INSERT INTO Group1 (name, admin) VALUES (?, ?)");
  // mysqli_report(MYSQLI_REPORT_ALL);
//   if ($groupStatement == FALSE) {
//     die ("Mysql Error: " . mysqli_error($connection));
// }
  mysqli_stmt_bind_param($groupStatement, "ss", $groupname, $username);
  $groupStatementSuccess = mysqli_stmt_execute($groupStatement);

  //it can go wrong if the username is not valid, because of the foreign key constraint
  //or if the groupname is too long
  if ($groupStatementSuccess) {
    $GID =  mysqli_insert_id($connection);
    echo $GID;
    $userGroupStatement = mysqli_prepare($connection, "INSERT INTO User_Group (GID, username) VALUES (?, ?)");
    mysqli_stmt_bind_param($userGroupStatement, "ss", $GID, $username);
    $userGroupStatementSuccess = mysqli_stmt_execute($userGroupStatement);

    if ($userGroupStatementSuccess) {
      $response["GID"] = $GID;
      $response["isError"] = FALSE;
      $response["success_msg"] = "You successfully created a new group";
    } else {
      $response["error_msg"] = "The user " . $username . " does not exist";
    }
    mysqli_stmt_close($userGroupStatement);
  } else {
    //@TODO: might need to be changed depending on name requirements or admin restrictions
    $response["error_msg"] = "Either the user is already admin of a group or the group name is invalid";
  }
  mysqli_stmt_close($groupStatement);

} else {
  $response["error_msg"] = "It must be a POST method";
}

echo json_encode($response);
mysqli_close($connection);

?>
