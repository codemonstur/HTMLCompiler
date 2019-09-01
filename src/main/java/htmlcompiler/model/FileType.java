package htmlcompiler.model;

import htmlcompiler.error.UnrecognizedFileType;

import java.util.HashMap;
import java.util.Map;

public enum FileType {
    html, stylesheet, less, sass, javascript, typescript;

    public static final Map<String, FileType> userInputToType = new HashMap<>();
    static {
        userInputToType.put("html", html);
        userInputToType.put("js", javascript);
        userInputToType.put("javascript", javascript);
        userInputToType.put("less", less);
        userInputToType.put("sass", sass);
        userInputToType.put("scss", sass);
        userInputToType.put("css", stylesheet);
        userInputToType.put("stylesheet", stylesheet);
        userInputToType.put("typescript", typescript);
        userInputToType.put("ts", typescript);
        userInputToType.put("text/html", html);
        userInputToType.put("text/css", stylesheet);
        userInputToType.put("text/less", less);
        userInputToType.put("text/sass", sass);
        userInputToType.put("text/javascript", javascript);
    }

    public static FileType fromUserInput(final String userInput) {
        return userInputToType.get(userInput);
    }

    public static FileType detectType(final String filename) throws UnrecognizedFileType {
        if (filename == null || filename.isEmpty()) throw new UnrecognizedFileType(filename);
        if (filename.endsWith(".html")) return html;
        if (filename.endsWith(".htm")) return html;
        if (filename.endsWith(".min.js")) return javascript;
        if (filename.endsWith(".js")) return javascript;
        if (filename.endsWith(".ts")) return typescript;
        if (filename.endsWith(".min.css")) return stylesheet;
        if (filename.endsWith(".css")) return stylesheet;
        if (filename.endsWith(".less")) return less;
        if (filename.endsWith(".sass")) return sass;
        if (filename.endsWith(".scss")) return sass;
        throw new UnrecognizedFileType(filename);
    }
}