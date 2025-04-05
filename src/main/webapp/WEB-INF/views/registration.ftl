<#-- registration.ftl -->
<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration</title>
</head>
<body>

<form action="<@spring.url '/users/registration'/>" method="post">
    <div class="form-group">
        <label for="username">Username:</label>
        <@spring.formInput "user.username", 'class="form-control" id="username" required'/>
    </div>

    <div class="form-group">
        <label for="password">Password:</label>
        <@spring.formPasswordInput "user.password", 'class="form-control" id="password" required'/>
    </div>

    <button type="submit">Register</button>
</form>

</body>
</html>