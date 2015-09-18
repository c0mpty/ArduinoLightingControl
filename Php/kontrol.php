<?php
$param = $_GET["test"];
$myfile = fopen("newfile.txt", "w") or die("Unable to open file!");
fwrite($myfile, $param);
fclose($myfile);
echo $param;
?>