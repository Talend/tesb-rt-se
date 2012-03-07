<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Social.com User Registration Form</title>
</head>
<body>
<h1>Social.com User Registration Form</h1>
<em></em>
<p>
 <table>
     <form action="/services/social/registerUser" method="POST">
        <tr>
            <td>User Name:</td>
            <td>
              <input type="text" name="user" value="barry@social.com"/>
            </td>
        </tr>
        <tr>
            <td>User Password:</td>
            <td>
              <input type="password" name="password" value="1234"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Register With Social.com"/>
            </td>
        </tr>
  </form>
 </table> 
</body>
</html>
