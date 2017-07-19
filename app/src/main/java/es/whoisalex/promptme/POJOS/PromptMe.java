package es.whoisalex.promptme.POJOS;

/**
 * Created by Alex on 16/07/2017.
 */

public class PromptMe {

    long id;
    String Frase;

    public PromptMe(String frase) {
        Frase = frase;
    }

    public PromptMe(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrase() {
        return Frase;
    }

    public void setFrase(String frase) {
        Frase = frase;
    }

    @Override
    public String toString() {
        return "PromptMe{" +
                "id=" + id +
                ", Frase='" + Frase + '\'' +
                '}';
    }
}
