package ar.edu.itba.paw.webapp.form;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class GetRestaurantsWithReports extends PagingForm {

    @QueryParam("withUnhandledReports")
    @DefaultValue("true")
    private boolean withUnhandledReports;

    public boolean isWithUnhandledReports() {
        return withUnhandledReports;
    }

    public void setWithUnhandledReports(boolean withUnhandledReports) {
        this.withUnhandledReports = withUnhandledReports;
    }
}
