<%@ page import="javax.servlet.http.HttpServletRequest,org.apache.cxf.rs.security.oauth.data.OAuthAuthorizationData,org.apache.cxf.rs.security.oauth.data.Permission" %>

<%
    OAuthAuthorizationData data = (OAuthAuthorizationData)request.getAttribute("data");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Third Party Authorization Form</title>
</head>
<body>
<title align="center">Third Party Authorization Form</title>
<table align="center">
       <tr align="center">
                <td>

                    <form action="<%= data.getReplyTo() %>" method="POST">
                        <input type="hidden" name="oauth_token"
                               value="<%= data.getOauthToken() %>"/>
                        <input type="hidden"
                               name="<%= org.apache.cxf.rs.security.oauth.utils.OAuthConstants
                                   .AUTHENTICITY_TOKEN %>"
                               value="<%= data.getAuthenticityToken() %>"/>

                        <p><b><%= data.getApplicationName() %></b> (<%= data.getApplicationDescription() %>)
                        
                        <br/> 
                        <img src="<%= data.getLogoUri() %>" alt="Application Logo" width="60" height="60">
                        <br/></p>
                        requests the following permissions:
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
                                  ><%= perm.getDescription() %></input>
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
                        <br/></p><br/>
                        <button name="<%= org.apache.cxf.rs.security.oauth.utils.OAuthConstants
                            .AUTHORIZATION_DECISION_KEY %>"
                                type="submit"
                                value="<%= org.apache.cxf.rs.security.oauth.utils.OAuthConstants
                                    .AUTHORIZATION_DECISION_ALLOW %>">
                            OK
                        </button>
                        <button name="<%= org.apache.cxf.rs.security.oauth.utils.OAuthConstants
                            .AUTHORIZATION_DECISION_KEY %>"
                                type="submit"
                                value="<%= org.apache.cxf.rs.security.oauth.utils.OAuthConstants
                                    .AUTHORIZATION_DECISION_DENY %>">
                            No,thanks
                        </button>
                        
                    </form>
                </td>
            </tr>
        </table>
    
</body>
</html>
