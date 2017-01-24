package bandm8s.hagenberg.fh.bandm8s.models;

/**
 * Created by Felix on 24.01.2017.
 */

public class Message {
    private String id;
    private String text;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message(String id, String text, String name) {
        this.id = id;
        this.text = text;
        this.name = name;
    }
}
