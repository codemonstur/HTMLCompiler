package htmlcompiler.pojos.compile;

public enum MoveType {
    body_end, body_start, head_end, head_start;

    public static MoveType toMoveType(final String name, final MoveType _default) {
        if (name == null || name.isEmpty()) return _default;

        try {
            return MoveType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return _default;
        }
    }

    public static void storeCode(final String code, final MoveType type, final ScriptBag scripts) {
        if (code == null || code.isEmpty() || type == null) return;

        switch (type) {
            case body_end: scripts.addScriptAtEndOfBody(code); break;
            case body_start: scripts.addScriptAtStartOfBody(code); break;
            case head_end: scripts.addScriptAtEndOfHead(code); break;
            case head_start: scripts.addScriptAtStartOfHead(code); break;
        }
    }
}
