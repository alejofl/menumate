package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class HandleReportForm {
    @NotNull
    private Long reportId;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }
}
