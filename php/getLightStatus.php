<?php

include_once('connects.php');

$room = $_GET['room'];
$query = "SELECT status FROM lights WHERE room_name='$room' LIMIT 1";
$result = mysqli_query($con, $query);

if ($row = mysqli_fetch_assoc($result)) {
    echo $row['status']; //will echo "0" or "1"
} else {
    echo "Error: Cannot retrieve from DB.";
}
?>
