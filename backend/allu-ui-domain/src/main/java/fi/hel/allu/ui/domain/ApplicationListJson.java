package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class ApplicationListJson {
    private List<ApplicationJson> applicationList = new ArrayList<>();

    @Valid
    @NotEmpty (message="{applicationList.size}")
    public List<ApplicationJson> getApplicationList() {
        return applicationList;
    }

    public void setApplicationJsonList(List<ApplicationJson> applicationList) {
        this.applicationList = applicationList;
    }
}
