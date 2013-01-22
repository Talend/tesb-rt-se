<%@ page import="javax.servlet.http.HttpServletRequest,org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData,org.apache.cxf.rs.security.oauth2.common.Permission" %>

<%
    OAuthAuthorizationData data = (OAuthAuthorizationData)request.getAttribute("data");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Third Party Authorization Form</title>
    <STYLE TYPE="text/css">
	<!--
	  input,button {font-family:verdana, arial, helvetica, sans-serif;font-size:20px;line-height:40px;} 
	-->
</STYLE>
</head>
<body>
<title align="center">Third Party Authorization Form</title>
<table align="center">
       <tr align="center">
                <td>

                    <form action="<%= data.getReplyTo() %>" method="POST">
                    
                        <input type="hidden" name="client_id"
                               value="<%= data.getClientId() %>"/>
                        <input type="hidden" name="state"
                               value="<%= data.getState() %>"/>
                        <input type="hidden" name="scope"
                               value="<%= data.getProposedScope() %>"/>
                        <input type="hidden" name="redirect_uri"
                               value="<%= data.getRedirectUri() %>"/>              
                        <input type="hidden"
                               name="<%= org.apache.cxf.rs.security.oauth2.utils.OAuthConstants
                                   .SESSION_AUTHENTICITY_TOKEN %>"
                               value="<%= data.getAuthenticityToken() %>"/>
                        <p><big><big><big><%= data.getApplicationName() %><br/>(<%= data.getApplicationDescription() %>)</big></big></big>
                        
                        <br/><br/> 
                        <img src="<%= data.getApplicationLogoUri() %>" alt="Application Logo" width="100" height="100">
                        <br/></p>
                        <big><big>requests the following permissions:<big/></big>
                        <p/>
                        <table> 
                            <%
                               for (Permission perm : data.getPermissions()) {
                            %>
                               <tr>
                                <td>
                                  <input type="checkbox" 
                                    <%
                                      if (perm.isDefault()) {
                                    %>
                                    disabled="disabled"
                                    <%
                                      }
                                    %> 
                                    checked="checked"
                                    name="<%= perm.getPermission()%>_status" 
                                    value="allow"
                                  ><big><big><%= perm.getDescription() %></big></big></input>
                                    <%
                                      if (perm.isDefault()) {
                                    %>
                                    <input type="hidden" name="<%= perm.getPermission()%>_status" value="allow" />
                                    <%
                                      }
                                    %>
                                </td>
                               </tr>
                            <%   
                               }
                            %> 
                        </table>    
                        <br/></p>
                        <button name="<%= org.apache.cxf.rs.security.oauth2.utils.OAuthConstants
                            .AUTHORIZATION_DECISION_KEY %>"
                                type="submit"
                                value="<%= org.apache.cxf.rs.security.oauth2.utils.OAuthConstants
                                    .AUTHORIZATION_DECISION_ALLOW %>">
                            OK
                        </button>
                        <button name="<%= org.apache.cxf.rs.security.oauth2.utils.OAuthConstants
                            .AUTHORIZATION_DECISION_KEY %>"
                                type="submit"
                                value="<%= org.apache.cxf.rs.security.oauth2.utils.OAuthConstants
                                    .AUTHORIZATION_DECISION_DENY %>">
                            No,thanks
                        </button>
                        
                    </form>
                </td>
            </tr>
        </table>
    
</body>
</html>
