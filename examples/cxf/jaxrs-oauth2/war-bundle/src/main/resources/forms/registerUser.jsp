<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Social.com User Registration Form</title>
    <STYLE TYPE="text/css">
	<!--
	  input {font-family:verdana, arial, helvetica, sans-serif;font-size:20px;line-height:40px;}
	  div.padded {  
         padding-left: 20em;  
      } 
	-->
</STYLE>
</head>
<body>
<div class="padded">
<h1>Social.com User Registration Form</h1>
<em></em>
<br/>
<p>
 <table>
     <form action="/examples/register/registerUser" method="POST">
        <tr>
            <td><big><big><big>User Name:</big></big></big></td>
            <td>
              <input type="text" name="user" value="barry@social.com"/>
            </td>
        </tr>
        <tr>
            <td><big><big><big>User Password:</big></big></big></td>
            <td>
              <input type="password" name="password" value="1234"/>
            </td>
        </tr>
        <tr>
            <td>
              &nbsp;
            </td>
            <td>
              &nbsp;
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Register With Social.com"/>
            </td>
        </tr>
  </form>
 </table> 
 </div>
</body>
</html>
