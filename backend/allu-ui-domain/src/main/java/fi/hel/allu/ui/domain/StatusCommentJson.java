package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.CommentType;

public class StatusCommentJson {

    private CommentType type;
    private String comment;

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
}
