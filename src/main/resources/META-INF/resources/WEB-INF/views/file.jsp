<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>File Upload Screen</title>
  </head>
  <body>
  	<h2>File Upload</h2>
  	<hr>
  	<form action="./upload" method="post" enctype="multipart/form-data">
  		<label for="file">Choose a file: </label>
  		<input type="file" id="file" name="file" />
  		<br><br>
  		<input type="submit" value="Upload" />
  	</form>
  	<br>
  	${message}
  </body>
</html>