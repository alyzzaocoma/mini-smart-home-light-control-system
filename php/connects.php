<?php


$DB_HOST = "localhost";
$DB_USER = "root"; 
$DB_PASS = ""; 
$DB_NAME = "smarthomescreen";

$con = mysqli_connect($DB_HOST, $DB_USER, $DB_PASS, $DB_NAME);

if (!$con) {
    die("Error: Failed to connect to database: " . mysqli_connect_error());
}
?>
