package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object to transfer applications between browser and ui backend.
 */
public class ApplicationDTO{
    /**
     * List of applications
     * in Finnish: Lista hakemuksia
     */
    @NotEmpty(message = "{applicationList.size}")
    @Valid
    private List<Application> applicationList = new ArrayList<>();

    public List<Application> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<Application> applicationList) {
        this.applicationList = applicationList;
    }
}
