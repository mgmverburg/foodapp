<?php

function GetGroup($connection) {
  $GID = $_POST["GID"];
  $groupStatement = mysqli_prepare($connection, "SELECT * FROM Group1 WHERE GID = ?");
  mysqli_stmt_bind_param($groupStatement, "i", $GID);
  $groupStatementSuccess = mysqli_stmt_execute($groupStatement);
  $groupResult = mysqli_stmt_get_result($groupStatement);
  mysqli_stmt_close($groupStatement);
  return $groupResult;
}

?>
