package hykar.projects.rspr.compiler;

import hykar.projects.rspr.compiler.handler.HtmlImageHandler;
import hykar.projects.rspr.compiler.handler.MessageHandler;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageCompiler {

    private static final Pattern PATTERN_TAG = Pattern.compile("!(\\w+)\\[\\s*(\\w+\\s*=\\s*\".*\"\\s*,?\\s*)*\\]");
    private static final Pattern PATTERN_TAG_ARGUMENTS = Pattern.compile("\\s*(\\w+)\\s*=\\s*\"([^\"]*)\"");
    private HashMap<String, MessageHandler> handlers;

    public MessageCompiler(Collection<MessageHandler> handleList) {
        HashMap<String, MessageHandler> hList = new HashMap<>();

        handleList.forEach(h -> hList.put(h.getTag(), h));

        handlers = hList;
    }

    public static MessageCompiler createDefault() {
        List<MessageHandler> handlers = new LinkedList<>();

        handlers.add(new HtmlImageHandler());
        return new MessageCompiler(handlers);
    }

    public String compile(String message) {
        Matcher matcher = PATTERN_TAG.matcher(message);
        StringBuilder builder = new StringBuilder();

        int last = 0;
        while (matcher.find()) {

            int start = matcher.start();
            int end = matcher.end();

            String tag = matcher.group(1);
            String args = matcher.group(2);

            Map<String, String> argsDictionary = new HashMap<>();

            Matcher argsMatcher = PATTERN_TAG_ARGUMENTS.matcher(args);
            while (argsMatcher.find())
                argsDictionary.put(argsMatcher.group(1), argsMatcher.group(2));

            builder.append(message.substring(last, start));

            if (handlers.containsKey(tag))
                builder.append(handlers.get(tag).handle(argsDictionary));

            last = end;

        }

        builder.append(message.subSequence(last, message.length()));

        return builder.toString();
    }
}
