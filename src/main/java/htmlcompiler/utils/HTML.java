package htmlcompiler.utils;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public enum HTML {;
    public static final HtmlCompressor HTML_COMPRESSOR = newDefaultHtmlCompressor();

    private static HtmlCompressor newDefaultHtmlCompressor() {
        final HtmlCompressor compressor = new HtmlCompressor();
        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        return compressor;
    }

    public static final Set<String> known_tags = Set.of("a", "abbr", "address", "area", "article", "aside",
        "audio", "b", "base", "bdi", "bdo", "blockquote", "body", "br", "button", "canvas", "caption", "svg",
        "cite", "code", "col", "colgroup", "data", "datalist", "dd", "del", "details", "dfn", "dialog",
        "div", "dl", "dt", "em", "embed", "fieldset", "figure", "footer", "form", "h1", "h2", "h3", "h4",
        "h5", "h6", "head", "header", "hgroup", "hr", "html", "i", "iframe", "img", "input", "ins", "kbd",
        "keygen", "label", "legend", "li", "link", "main", "map", "mark", "menu", "menuitem", "meta", "meter",
        "nav", "noscript", "object", "ol", "optgroup", "option", "output", "p", "param", "pre", "progress",
        "q", "rb", "rp", "rt", "rtc", "ruby", "s", "samp", "script", "section", "select", "small", "source",
        "span", "strong", "style", "sub", "summary", "sup", "table", "tbody", "td", "template", "textarea",
        "tfoot", "th", "thead", "time", "title", "tr", "track", "u", "ul", "var", "video", "wbr", "#root",
        "path", "lineargradient", "stop", "g", "defs", "rect", "circle", "fecolormatrix", "fegaussianblur",
        "feoffset", "filter", "radialgradient", "center", "marquee", "blink", "frame", "frameset");

    // https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes
    public static final Set<String> known_attributes = Set.of("accept", "accept-charset", "accesskey",
        "action", "align", "allow", "alt", "async", "autocapitalize", "autocomplete", "autofocus",
        "autoplay", "background", "bgcolor", "buffered", "challenge", "charset", "checked", "cite",
        "class", "code", "codebase", "color", "cols", "colspan", "content", "contenteditable", "contextmenu",
        "controls", "coords", "crossorigin", "csp", "data", "datetime", "decoding", "default", "defer", "d",
        "dir", "dirname", "disabled", "download", "draggable", "dropzone", "enctype", "enterkeyhint",
        "for", "form", "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget", "headers",
        "height", "hidden", "high", "href", "hreflang", "http-equiv", "icon", "id", "importance",
        "integrity", "intrinsicsize", "inputmode", "ismap", "itemprop", "keytype", "kind", "label",
        "lang", "language", "loading", "list", "loop", "low", "manifest", "max", "maxlength", "minlength",
        "media", "method", "min", "multiple", "muted", "name", "novalidate", "open", "optimum", "pattern",
        "ping", "placeholder", "poster", "preload", "radiogroup", "readonly", "referrerpolicy", "rel",
        "required", "reversed", "rows", "rowspan", "sandbox", "scope", "scoped", "selected", "shape",
        "size", "sizes", "slot", "span", "spellcheck", "src", "srcdoc", "srclang", "srcset", "start",
        "step", "style", "summary", "tabindex", "target", "title", "translate", "type", "usemap", "value",
        "width", "wrap", "onafterprint", "onbeforeprint", "onbeforeunload", "onerror", "onhashchange",
        "onload", "onmessage", "onoffline", "ononline", "onpagehide", "onpageshow", "onpopstate",
        "onresize", "onstorage", "onunload", "role", "aria-valuenow", "aria-valuemin", "aria-valuemax",
        "aria-haspopup", "aria-expanded", "aria-hidden", "aria-controls", "aria-label", "xmlns",
        "viewbox", "fill", "fill-rule", "aria-labelledby", "ondrop", "ondragover", "aria-multiselectable",
        "property", "stop-color", "stop-opacity", "offset", "x1", "y1", "x2", "y2", "rx", "x",
        "transform", "values", "in", "stdDeviation", "result", "y", "filterunits", "onclick",
        "pointer-events", "stroke", "stroke-miterlimit", "stroke-width", "visibility", "allowfullscreen",
        "frameborder", "fill-opacity", "nonce");

    // https://uzzal.wordpress.com/2009/10/08/fobidden-deprecated-html-tags-and-attributes/
    // https://www.tutorialspoint.com/html5/html5_deprecated_tags.htm
    public static final Set<String> deprecated_tags = Set.of("acronym", "applet", "basefont", "big", "center"
            , "dir", "embed", "font", "frame", "frameset", "isindex", "noframes", "menu", "noembed", "s", "strike", "tt", "u");

    // http://www.w3.org/TR/html4/index/elements.html
    // http://www.w3.org/TR/html4/index/attributes.html
    // https://uzzal.wordpress.com/2009/10/08/fobidden-deprecated-html-tags-and-attributes/
    // https://www.tutorialspoint.com/html5/html5_deprecated_tags.htm
    public static final Map<String, Set<String>> deprecated_attributes = ofEntries
        ( entry("abbr", Set.of("td", "t"))
        , entry("align", Set.of("caption", "iframe", "img", "input", "legend", "object", "table", "hr", "div", "h1"
        , "h2", "h3", "h4", "h5", "h6", "p", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("alink", Set.of("body"))
        , entry("axis", Set.of("td", "t"))
        , entry("archive", Set.of("object"))
        , entry("background", Set.of("body"))
        , entry("bgcolor", Set.of("body", "table", "th", "tr", "td"))
        , entry("border", Set.of("img", "object", "table"))
        , entry("cellpadding", Set.of("table"))
        , entry("cellspacing", Set.of("table"))
        , entry("char", Set.of("col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("charoff", Set.of("col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("charset", Set.of("link", "a"))
        , entry("classid", Set.of("object"))
        , entry("clear", Set.of("br"))
        , entry("codebase", Set.of("object"))
        , entry("codetype", Set.of("object"))
        , entry("compact", Set.of("ol", "ul", "dl", "menu"))
        , entry("coords", Set.of("a"))
        , entry("declare", Set.of("object"))
        , entry("frame", Set.of("table"))
        , entry("frameborder", Set.of("iframe"))
        , entry("hspace", Set.of("img", "object"))
        , entry("link", Set.of("body"))
        , entry("longdesc", Set.of("img", "iframe"))
        , entry("marginheight", Set.of("iframe"))
        , entry("marginwidth", Set.of("iframe"))
        , entry("name", Set.of("img"))
        , entry("nohref", Set.of("area"))
        , entry("noshade", Set.of("hr"))
        , entry("nowrap", Set.of("td", "tr", "th"))
        , entry("profile", Set.of("head"))
        , entry("rev", Set.of("link", "a"))
        , entry("rules", Set.of("table"))
        , entry("scheme", Set.of("meta"))
        , entry("scope", Set.of("td"))
        , entry("scrolling", Set.of("iframe"))
        , entry("shape", Set.of("a"))
        , entry("size", Set.of("basefont", "font", "hr"))
        , entry("standby", Set.of("object"))
        , entry("start", Set.of("ol"))
        , entry("target", Set.of("link"))
        , entry("text", Set.of("body"))
        , entry("type", Set.of("li", "param", "ol", "ul"))
        , entry("valign", Set.of("col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"))
        , entry("value", Set.of("li"))
        , entry("valuetype", Set.of("param"))
        , entry("version", Set.of("html"))
        , entry("vlink", Set.of("body"))
        , entry("vspace", Set.of("img", "object"))
        , entry("width", Set.of("hr", "pre", "td", "th", "table", "col", "colgroup"))
        );

    public static final Set<String> event_handler_attributes = Set.of("onafterprint", "onbeforeprint"
            , "onbeforeunload", "onerror", "onhashchange", "onload", "onmessage", "onoffline", "ononline"
            , "onpagehide", "onpageshow", "onpopstate", "onresize", "onstorage", "onunload");

    // https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input
    // https://www.w3schools.com/html/html_form_input_types.asp
    public static final Set<String> known_input_types = Set.of("button", "checkbox", "color", "date",
            "datetime-local", "email", "file", "hidden", "image", "month", "number", "password", "radio",
            "range", "reset", "search", "submit", "tel", "text", "time", "url", "week", "datetime");

}
