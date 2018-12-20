package hykar.projects.rspr.enums;

public enum TokenType {

    ACTIVATION("ACTIVATION",0);

    private int type;
    private String typeName;

     TokenType(String typeName,int type)
    {
        this.typeName = typeName;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public static TokenType resolveTokenType(int type)
    {
        for(TokenType t : TokenType.values())
            if(t.type == type) return t;

        return null;
    }
}
