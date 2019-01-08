<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>WEB应用</title>

</head>
<body class="hold-transition index-page">

    <div class="index-box">
        <p>当前用户为:<#if ssoUser??>${ssoUser.userName}</#if></p>
        <p><a href="${ssoLogoutUrl!''}">退出</a></p>
    </div>

</body>
</html>