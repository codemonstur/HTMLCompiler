package htmlcompiler.tools;

import java.util.Set;

public enum HTML {;

    public static final Set<String> known_tags = Set.of("a", "abbr", "address", "area", "article", "aside",
        "audio", "b", "base", "bdi", "bdo", "blockquote", "body", "br", "button", "canvas", "caption",
        "cite", "code", "col", "colgroup", "data", "datalist", "dd", "del", "details", "dfn", "dialog",
        "div", "dl", "dt", "em", "embed", "fieldset", "figure", "footer", "form", "h1", "h2", "h3", "h4",
        "h5", "h6", "head", "header", "hgroup", "hr", "html", "i", "iframe", "img", "input", "ins", "kbd",
        "keygen", "label", "legend", "li", "link", "main", "map", "mark", "menu", "menuitem", "meta", "meter",
        "nav", "noscript", "object", "ol", "optgroup", "option", "output", "p", "param", "pre", "progress",
        "q", "rb", "rp", "rt", "rtc", "ruby", "s", "samp", "script", "section", "select", "small", "source",
        "span", "strong", "style", "sub", "summary", "sup", "table", "tbody", "td", "template", "textarea",
        "tfoot", "th", "thead", "time", "title", "tr", "track", "u", "ul", "var", "video", "wbr", "#root");

    // https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes
    public static final Set<String> known_attributes = Set.of("accept", "accept-charset", "accesskey",
        "action", "align", "allow", "alt", "async", "autocapitalize", "autocomplete", "autofocus",
        "autoplay", "background", "bgcolor", "buffered", "challenge", "charset", "checked", "cite",
        "class", "code", "codebase", "color", "cols", "colspan", "content", "contenteditable", "contextmenu",
        "controls", "coords", "crossorigin", "csp", "data", "datetime", "decoding", "default", "defer",
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
        "aria-haspopup", "aria-expanded", "aria-hidden", "aria-controls", "aria-label");

}
