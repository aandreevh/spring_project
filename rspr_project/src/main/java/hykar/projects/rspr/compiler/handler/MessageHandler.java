package hykar.projects.rspr.compiler.handler;

import java.util.Map;

public interface MessageHandler {

    String handle(Map<String,String> arguments);
    String getTag();
}
