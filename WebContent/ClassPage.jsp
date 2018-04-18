<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="stylesheet" type="text/css" href="style/ClassPage.css">
		<title>CourseMash</title>
		<%@ page import="java.util.*" %>
		<%@ page import="java.io.*" %>
		<%@ page import="CS201GroupProject.JDBCQuery"%>
		<%@ page import="CS201GroupProject.Course"%>
		<%@ page import="CS201GroupProject.User"%>
		<%@ page import= "javax.servlet.http.HttpSession" %>
		<%@ page import= "CS201GroupProject.Post" %>
		
		<% 
			User currUser = ((User)request.getSession().getAttribute("currUser"));
			String currClass = request.getParameter("classid"); //whatever gets passed through
			Integer classid = (Integer.parseInt(currClass));
			Vector<Post> posts = JDBCQuery.getPostsByCourseID(classid); //vector of posts
			
		
		
		%>
		<script>
			function postValidate() {
				var xhttp = new XMLHttpRequest();
				xhttp.onreadystatechange = function() {
				    if (this.readyState == 4 && this.status == 200) {
				       // Typical action to be performed when the document is ready:
				    	   if(xhttp.responseText === "valid") {
				    		   location.reload();
				    	   }
				    	   else {
				    		   document.getElementById("postDetails").innerHTML = xhttp.responseText;
				    	   }
				    }
				};
				var title = document.getElementById("title").value;
				var body = document.getElementById("body").value;
				xhttp.open('GET', '/CS201Project/validPost?title=' + title + '&body=' + body + '&courseID=' + <%= currClass %>, true);
				xhttp.send();
			}
		</script>
		
		<script>
			
			function createPost(){
				if(<%= currUser == null %>) {
					return;
				}
				
				var courseID = <%= classid %>;
				var newLine = '<br>';
				var code = '';
				var startForm = '<form name="newPostForm" id="newPostForm" method="GET" action="validPost">';
				//title, body
				var endForm = '</form>';
				//title code
				var title = '<input type="text" name="title" id="title" placeholder="title">';
				//var titleError = '<span style="color: red;font-weight:bold; position:fixed; margin-top: 2px; margin-left: 2px;">${title_err!=null? title_err : ''}</span>';
				//body
				//var body = '<input type="text" name="body" id="body" placeholder = "body">';
				var body = '<textarea name="comment" id ="body" form="newPostForm"></textarea>';
				//var bodyError = '<span style="color: red;font-weight:bold; position:fixed; margin-top: 2px; margin-left: 2px;">${body_err!=null? body_err : ''}</span>';
				//submit
				var submit = '<input type="button" id="button" onclick="postValidate()" value="Submit">';
				
				var classid = '<input type="hidden" name="courseID" value="' + courseID + '">';
				
				code += startForm;
				code += (title + newLine);
				code += (body + newLine);
				code += submit;
				code += classid;
				code += endForm;
				
				document.getElementById("postDetails").innerHTML = code;
				//need to send classid as a hidden
			}
		</script>
	
	</head>
	<body>
		<h1> <a href="UserClassList.jsp"> CourseMash </a> </h1>
		
		
		<div id="scrollable-content">
			<div id="content"></div>
		</div>
		<script>
			var CourseInfo = '';
			var CourseProf = '';

			var table = '<table class="courseTable"><tr>';
			var newLine = '</tr><tr>';


			<% for (int i = 0; i < posts.size(); i++) { %>

				var postTitle = "<%= posts.get(i).getTitle() %>";
				var postID = "<%= posts.get(i).getPostID() %>";
				
				var startButton = '<button onclick="getPost(' + postID + ')">';
				var endButton = '</button>';
				var startInput = '<i';

				table += (startButton + postTitle + endButton + newLine + '<br>');
	<%		} %>
			table += '</table>';
			
			if(<%= currUser != null %>) {
				table += '<button id="createPost" onclick="createPost()">Create New Post</button>';
			}
			document.getElementById("content").innerHTML = table; //display table
		</script>
		
		<div id="postDetails"></div>
		<script>
			function getPost(postID) {
				if(<%= currUser == null %>) {
					return;
				}
				var xhttp = new XMLHttpRequest();
				xhttp.onreadystatechange = function() {
				    if (this.readyState == 4 && this.status == 200) {
				       // Typical action to be performed when the document is ready:
				       document.getElementById("postDetails").innerHTML = xhttp.responseText;
				    }
				};
				xhttp.open('GET', '/CS201Project/GetPost?postid=' + postID, true);
				xhttp.send();
			}
		</script>
	</body>
</html>
