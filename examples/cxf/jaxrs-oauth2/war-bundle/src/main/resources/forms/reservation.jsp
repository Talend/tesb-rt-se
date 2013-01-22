<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Restaurant Reservations Form</title>
    <STYLE TYPE="text/css">
	<!--
	  input {font-family:verdana, arial, helvetica, sans-serif;font-size:20px;line-height:40px;}
	  div.padded {  
         padding-left: 15em;  
      } 
	-->
</STYLE>
</head>
<body>
<div class="padded">
<h1>Welcome to Restaurant Reservations Online Service</h1>
<em></em>
<p>
 <table>
     <form action="/examples/reservations/reserve/table" method="POST">
        <tr>
            <td><big><big><big>Customer Name:</big></big></big></td>
            <td>
              <input type="text" name="name" value="Barry"/>
            </td>
        </tr>
        <tr>
            <td><big><big><big>Customer phone:</big></big></big></td>
            <td>
              <input type="text" name="phone" value="12345678"/>
            </td>
        </tr>
        <tr>
            <td><big><big><big>Hour (p.m):</big></big></big></td>
            <td>
              <input type="text" name="hour" value="7"/>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="        Reserve       "/>
            </td>
        </tr>
  </form>
 </table> 
</div> 
</body>
</html>
