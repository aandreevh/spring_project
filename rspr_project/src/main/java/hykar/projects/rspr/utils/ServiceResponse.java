package hykar.projects.rspr.utils;

import java.io.Serializable;

public class ServiceResponse implements Serializable {

    public static int MESSAGE=0;
    public static int WARNING=1;
    public static int ERROR=2;

    private int type;
    private String message;

    public ServiceResponse(int type, String message)
    {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public static ServiceResponse message(String message)
    {
        return new ServiceResponse(MESSAGE,message);
    }

    public static ServiceResponse error(String message)
    {
        return new ServiceResponse(ERROR,message);
    }

    public static ServiceResponse warning(String message)
    {
        return new ServiceResponse(WARNING,message);
    }

}
