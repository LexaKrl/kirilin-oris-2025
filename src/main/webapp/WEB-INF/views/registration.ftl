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
        <input id="username" type="text" name="username">
    </div>

    <div class="form-group">
        <label for="password">Password:</label>
        <input id="password" type="text" name="password">
    </div>

    <button type="submit">Register</button>
</form>

</body>
</html>