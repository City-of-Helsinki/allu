package fi.hel.allu.ui.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FreeEventApplicationJson {

    /**
     *
     * in Finnish: Hakemuksen asiakas
     */
    @NotNull
    @Valid
    private Customer customer;

    /**
     * Project that application belongs to
     * in Finnish: Hanke, johon hakemus liittyy
     */
    @Valid
    private Project project;

}
