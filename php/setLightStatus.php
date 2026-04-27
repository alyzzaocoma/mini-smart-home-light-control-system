<?php 

include_once('connects.php');

$room = $_GET['room'];        //which room
$status = $_GET['status']; //0 or 1

$query = "UPDATE lights SET status = '$status' WHERE room_name = '$room'";
$result = mysqli_query($con, $query);

if ($result) {
    echo $room . "updated"; //for error handling only
} else {
    echo "Error: Unable to update.";
}
?>
