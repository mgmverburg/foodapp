<?php
  define("DB_HOST", getenv('OPENSHIFT_MYSQL_DB_HOST')); //this is a special openshift environment variable
  //you can look up environment variables openshift to find out more, but basically
  //it retrieves the variables and this only works since the php is on the same 'server'/'location'/'app' as the
  //MySQL database. So keep that in mind, because making changes somehow in the organization
  //of where the PHP app is stored and where the MySQL app is stored might affect this and break the code.
  define("DB_USER", "adminbdsIK34");
  define("DB_PASSWORD", "vQrqrAGfeIfZ");
  define("DB_DATABASE", "foodapp");
  define("DB_PORT", getenv('OPENSHIFT_MYSQL_DB_PORT'));


	$connection = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE, DB_PORT) or die('Unable to Connect');

?>
