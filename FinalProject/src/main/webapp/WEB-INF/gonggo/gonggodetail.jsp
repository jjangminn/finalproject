<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
</head>
<body>
<form class="detail form form-control">
<img src="${dto.empimg}">
<span>${dto.jobgroup}</span><br>
${dto.deadline}<br>
${dto.empcontent}<br>

<button class="update" >공고 수정</button>
<button type="button"class="delete" onclick="location.href='delete?num=${dto.num}'">공고 삭제</button>
</form>
</body>
</html>