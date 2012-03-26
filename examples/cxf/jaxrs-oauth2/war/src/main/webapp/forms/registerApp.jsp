<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Client Application Registration Form</title>
</head>
<body>
<h1>Client Application Registration Form</h1>
<em></em>
<p>

 <table>
     <form action="/services/oauth/registerProvider"
           enctype="multipart/form-data" 
           method="POST">
        <tr>
            <td><big><big><big>Application Name:</big></big></big></td>
            <td>
              <input type="text" name="appName" size="80" value="Restaurant Reservations"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td><big><big><big>Application Description:</big></big></big></td>
            <td>
              <input type="text" size="80" name="appDescription" 
                     value="The online service for booking a table at the favourite restaurant"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td><big><big><big>Application URI:</big></big></big></td>
            <td>
              <input type="text" size="80" name="appURI" value="http://localhost:${http.port}/services/reservations"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td><big><big><big>Application Logo:</big></big></big></td>
            <td>
               <input id="appLogo" size="80" name="appLogo" type="file" accept="image/gif,image/jpeg,image/png"/>
            </td>
        </tr>
        <tr>
            <td>
              &nbsp;
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="    Register Your Application    "/>
            </td>
        </tr>
  </form>
 </table>
  
</body>
</html>
