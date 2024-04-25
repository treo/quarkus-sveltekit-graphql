package tech.dubs.jax.chat.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class LoginResource {
    @GET
    @Path("login")
    public Response login(@QueryParam("redirect") String redirect){
        if(redirect == null || redirect.startsWith("/login")) redirect = "/";
        return Response.temporaryRedirect(URI.create(redirect)).build();
    }
}
