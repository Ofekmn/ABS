package engine.exception.xml;

public class NameException extends Exception {
    private final String type;
    private final String name;

    public NameException(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {return "There are 2 different instances of the " + getType() + " " + getName();}
}
