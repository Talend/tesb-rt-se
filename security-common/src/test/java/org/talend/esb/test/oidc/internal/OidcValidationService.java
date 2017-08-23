package org.talend.esb.test.oidc.internal;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
@Produces("application/json")
public class OidcValidationService {

    public static final String ENDPOINT = "local://validationservice/";

    @POST
    @Path("/")
    public Response getToken(String input) {
        System.out.println("[OidcValidationService] input: " + input);
        Response response = null;

        if (input.contains("token=valid_token")) {
            response = Response.ok().entity("{ \"active\" : \"true\" }").build();
        } else if (input.contains("token=invalid_json")) {
            response = Response.ok().entity("<this{is/>not}a<json").build();
        } else {
            response = Response.ok().entity("{ \"active\" : \"false\" }").build();
        }

        return response;
    }
}
