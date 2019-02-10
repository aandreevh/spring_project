package hykar.projects.rspr.compiler.handler;

import java.util.Map;

public class HtmlImageHandler implements MessageHandler {
    @Override
    public String handle(Map<String, String> arguments) {

        StringBuilder builder = new StringBuilder("<br><img");

        for (String key :
                arguments.keySet())
            builder.append(" " + key + "=\"" + arguments.get(key) + "\"");

        builder.append(">");
        builder.append("</img><br>");


        return builder.toString();
    }

    @Override
    public String getTag() {
        return "image";
    }
}
