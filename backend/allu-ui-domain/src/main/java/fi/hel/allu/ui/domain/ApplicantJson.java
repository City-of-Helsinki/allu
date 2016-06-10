package fi.hel.allu.ui.domain;

public class ApplicantJson extends TypeJson {

    private Integer id;

    /**
     * in Finnish: Hakijan tunniste
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
