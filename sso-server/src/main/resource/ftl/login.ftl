<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>统一认证中心</title>

</head>
<body class="hold-transition login-page">

    <div class="login-box">
        <form action="${request.contextPath}/doLogin">
            <div class="login-box-body">
                <p class="login-box-msg">统一认证中心</p>
                <div class="form-group has-feedback">
                    <input type="text" name="userName" class="form-control" placeholder="Please input username." value="SsoUser0" maxlength="50" >
                    <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
                </div>
                <div class="form-group has-feedback">
                    <input type="password" name="password" class="form-control" placeholder="Please input password." value="SsoPwd0" maxlength="50" >
                    <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                </div>

                <div class="row">
                    <div class="col-xs-8">
                        <div class="checkbox icheck">
                            <label>
                                <input type="checkbox" name="rememberPwd" >记住密码
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-4">
                        <input type="hidden" name="redirectUrl" value="${redirectUrl!''}" />
                        <button type="submit" class="btn btn-primary btn-block btn-flat">Login</button>
                    </div>
                </div>
            </div>
        </form>
    </div>

</body>
</html>