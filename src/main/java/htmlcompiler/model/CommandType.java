package htmlcompiler.model;

public enum CommandType {
    compile, compress, diff, verify, unknown;

    public static CommandType toCommandType(final String[] args, final CommandType _default) {
        if (args == null || args.length == 0) return _default;
        try {
            return CommandType.valueOf(args[0]);
        } catch (Exception e) {
            return _default;
        }
    }
}
