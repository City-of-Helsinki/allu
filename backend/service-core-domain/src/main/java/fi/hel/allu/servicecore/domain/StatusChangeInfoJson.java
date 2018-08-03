package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.types.CommentType;

/**
 * Class containing additional info related to changing application's status
 */
public class StatusChangeInfoJson {

    private CommentType type;
    private String comment;
    private Integer owner;

    public StatusChangeInfoJson() {
    }

    public StatusChangeInfoJson(Integer owner, CommentType type, String comment) {
      this.type = type;
      this.comment = comment;
      this.owner = owner;
    }

    /**
     * Type of comment
     */
    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    /**
     * in Finnish: Kommentti
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Id of the new owner for application which status is changed
     */
    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }
}
