package htmlcompiler.pojos.compile;

public final class ScriptBag {
    public final Bag head = new Bag();
    public final Bag body = new Bag();

    public static class Bag {
        public final StringBuilder scriptsAtStart = new StringBuilder();
        public final StringBuilder scriptsAtEnd = new StringBuilder();
    }

    public void addScriptAtStartOfHead(final String script) {
        head.scriptsAtStart.append(script.trim());
    }
    public void addScriptAtEndOfHead(final String script) {
        head.scriptsAtEnd.append(script.trim());
    }
    public void addScriptAtStartOfBody(final String script) {
        body.scriptsAtStart.append(script.trim());
    }
    public void addScriptAtEndOfBody(final String script) {
        body.scriptsAtEnd.append(script.trim());
    }

    public String getScriptAtHeadStart() {
        return head.scriptsAtStart.toString();
    }
    public String getScriptAtHeadEnd() {
        return head.scriptsAtEnd.toString();
    }
    public String getScriptAtBodyStart() {
        return body.scriptsAtStart.toString();
    }
    public String getScriptAtBodyEnd() {
        return body.scriptsAtEnd.toString();
    }

}
