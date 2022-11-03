package fr.insee.sabianedata.ws.model.pearl;

public class CommentDto {

    private CommentType type;
    private String value;

    public CommentDto() {
    }

    public CommentDto(CommentType type, String value) {
        this.type = type;
        this.value = value;
    }

    public CommentType getType() {
        return this.type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
