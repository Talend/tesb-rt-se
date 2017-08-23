package org.talend.esb.test.oidc.internal;




import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
@Produces("application/json")
public class OidcTokenService {

    public static final String ENDPOINT = "local://tokenservice/";

    public static final String USER_INVALID_JSON = "user_invalid_json";
    public static final String USER_ERROR_500 = "user_error_500";
    public static final String USER_ERROR_500_2 = "user_err_500";
    public static final String USER_WRONG_TOKEN_TYPE = "user_wrong_token_type";


    @POST
    @Path("/")
    public Response getToken(String input) {
        System.out.println("[OidcTokenService] input: " + input);
        Response response;
        String responseJson = "";

        if (input.contains(USER_INVALID_JSON)) {
            responseJson = "<not}a/>json";
            response = Response.ok().entity(responseJson).build();
        } else if (input.contains(USER_ERROR_500)) {
            response = Response.serverError().status(500).entity("{\"error\" : \"servlet exception\" }").build();
        } else if (input.contains(USER_ERROR_500_2) ) {
            response = Response.serverError().status(500).entity("").build();
        } else if (input.contains(USER_WRONG_TOKEN_TYPE)) {
            response = Response.ok().entity("{\"token_type\" : \"YouNameIt\" }").build();
        } else {
            response = Response.ok().entity("{\"access_token\" : \"Access Granted!\",  \"token_type\" : \"Bearer\" }").build();
        }
        return response;
    }
}
