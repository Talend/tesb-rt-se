<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Client Application Registration Form</title>
    <STYLE TYPE="text/css">
	<!--
	  input {font-family:verdana, arial, helvetica, sans-serif;font-size:20px;line-height:40px;}
	  H1 { text-align: center}
	  div.padded {  
         padding-left: 5em;  
      }   
	-->
</STYLE>
</head>
<body>
<H1>Client Application Registration Form</H1>
<br/>
     <form action="/examples/oauth/registerProvider"
           enctype="multipart/form-data" 
           method="POST">
       <div class="padded">  
       <table>    
        <tr>
            <td><big><big><big>Application Name:</big></big></big></td>
            <td>
              <input type="text" name="appName" size="50" value="Restaurant Reservations"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td><big><big><big>Application Description:</big></big></big></td>
            <td>
              <input type="text" size="50" name="appDescription" 
                     value="The online service for booking a table at the favourite restaurant"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td><big><big><big>Application URI:</big></big></big></td>
            <td>
              <input type="text" size="50" name="appURI" value="http://localhost:${http.port}/examples/reservations"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td><big><big><big>Application Redirect URI:</big></big></big></td>
            <td>
              <input type="text" size="50" name="appRedirectURI" value="http://localhost:${http.port}/examples/reservations/reserve/complete"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td><big><big><big>Application Logo:</big></big></big></td>
            <td>
               <input id="appLogo" size="50" name="appLogo" type="file" accept="image/gif,image/jpeg,image/png"/>
            </td>
        </tr>
        <tr>
            <td>
              &nbsp;
            </td>
        </tr>
        </table>
        </div>
        <table align="center">
        <tr>
            <td colspan="2">
                <input type="submit" value="    Register Your Application    "/>
            </td>
        </tr>
        </table>
  </form>
 
  
</body>
</html>
