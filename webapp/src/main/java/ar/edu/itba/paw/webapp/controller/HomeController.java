package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.HomeIndexDto;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(UriUtils.API_BASE_URL)
@Component
public class HomeController {

    @Context
    private UriInfo uriUtils;

    @GET
    public Response homeIndex() {
        return Response.ok(HomeIndexDto.from(uriUtils)).build();
    }
}
