<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Restaurant Reservations Form</title>
</head>
<body>
<h1>Welcome to Restaurant Reservations Online Service</h1>
<em></em>
<p>
 <table>
     <form action="/services/reservations/reserve/table" method="POST">
        <tr>
            <td>Customer Name:</td>
            <td>
              <input type="text" name="name" value="Barry"/>
            </td>
        </tr>
        <tr>
            <td>Customer phone:</td>
            <td>
              <input type="text" name="phone" value="12345678"/>
            </td>
        </tr>
        <tr>
            <td>Hour (p.m):</td>
            <td>
              <input type="text" name="hour" value="7"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Reserve"/>
            </td>
        </tr>
  </form>
 </table> 
</body>
</html>
