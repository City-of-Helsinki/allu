package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;

/**
 * in Finnish: Ilmaistapahtuma
 */
public class FreeEventApplication {
    /**
     * in Finnish: Ilmaistapahtuman tunniste
     */
    private Integer id;

    /**
     * in Finnish: Tapahtuman nimi
     */
    private String name;

    /**
     * in Finnish: Viittaus yhteisiin hakemuksen tietoihin
     */
    private Integer applicationId;

    /**
     * in Finnish: Tapahtuman kuvaus
     */
    private String description;

    /**
     * in Finnish: Tapahtuman luonne
     */
    private String nature;

    /**
     * in Finnish: Tapahtuman alkupäivä
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime startTime;

    /**
     * in Finnish: Tapahtuman loppupäivä
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime endTime;

    /**
     * in Finnish: Tapahtuman www-sivu
     */
    private String url;

    /**
     * in Finnish: Tapahtuman arvioitu yleisömäärä
     */
    private Integer audience;

    /**
     * in Finnish: Tapahtuman arvioitu yleisömäärä
     */
    public Integer getAudience() {
        return audience;
    }

    public void setAudience(Integer audience) {
        this.audience = audience;
    }

    /**
     * in Finnish: Ilmaistapahtuman tunniste
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * in Finnish: Tapahtuman nimi
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * in Finnish: Viittaus yhteisiin hakemuksen tietoihin
     */
    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * in Finnish: Tapahtuman kuvaus
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * in Finnish: Tapahtuman luonne
     */
    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    /**
     * in Finnish: Tapahtuman alkupäivä
     */
    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * in Finnish: Tapahtuman loppupäivä
     */
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * in Finnish: Tapahtuman www-sivu
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
