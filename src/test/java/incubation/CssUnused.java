package incubation;

public final class CssUnused {
    private CssUnused() {}

/*
    This code was meant to detect and remove unused CSS from a given page.
    I doubt such a thing is even possible. What about html and classes created through JS?
    I don't think I'll ever pick this back up, and the library that I use for this fails
    with bob so that's another thing.

    <dependency>
        <groupId>com.helger</groupId>
        <artifactId>ph-css</artifactId>
        <version>6.2.0</version>
        <scope>test</scope>
    </dependency>

    bob can't find the version number for the dependency of this library:
    [ERROR] Failed to execute target 'build':
    [ERROR] Caused by: internalcommandfailed: Command run-target failed
    [ERROR] Caused by: dependencyresolutionfailed: Failed to create dependency tree for project 'htmlcompiler'
    [ERROR] Caused by: dependencyresolutionfailed: Failed to create dependency tree for dependency 'repository: com.helger:ph-css:6.2.0'
    [ERROR] Caused by: dependencyresolutionfailed: Failed to build POM dependency tree for com.helger:ph-css:6.2.0
    [ERROR] Caused by: invalidinput: dependency is missing version; com.helger:ph-commons

    Maven reports this version as 9.3.4:
    [INFO] +- com.helger:ph-css:jar:6.2.0:test
    [INFO] |  \- com.helger:ph-commons:jar:9.3.4:test

    Maybe I'll debug this one day.

    private static final Set<String> tags = new HashSet<>(Arrays.asList("body", "html", "b"));
    private static final Set<String> classes = new HashSet<>(Arrays.asList("classone", "classtwo"));
    private static final Set<String> ids = new HashSet<>(Arrays.asList("id", "button"));

    private static final Set<ICSSTopLevelRule> used = new HashSet<>();

    public static void main(final String... args) throws IOException, InvalidInput {
        final File file = new File("src/test/resources/test2.css");

        com.google.javascript.jscomp.Compiler google = new com.google.javascript.jscomp.Compiler(System.out);
        final SourceFile code = SourceFile.fromCode("hello", "console.log(\"Hello, world!\")");
        google.compile(null, code, newDefaultCompilerOptions());

        readWithPhax(file);
//        readWithCSSOMParser(file);
    }

    private static CompilerOptions newDefaultCompilerOptions() {
        final CompilerOptions options = new CompilerOptions();
        options.setFoldConstants(true);
        options.setCoalesceVariableNames(true);
        options.setDeadAssignmentElimination(true);
        options.setInlineConstantVars(true);
        options.setInlineFunctions(Reach.LOCAL_ONLY);
        options.setMaxFunctionSizeAfterInlining(50);
        options.setAssumeStrictThis(true);
        options.setAssumeClosuresOnlyCaptureReferences(true);
        options.setInlineProperties(true);
        options.setCrossChunkCodeMotion(true);
        options.setParentChunkCanSeeSymbolsDeclaredInChildren(true);
        options.setCrossChunkCodeMotion(true);
        options.setRemoveUnusedPrototypeProperties(true);
        options.setInlineVariables(Reach.LOCAL_ONLY);
        options.setInlineLocalVariables(true);
        options.setSmartNameRemoval(true);
        options.setExtraSmartNameRemoval(true);
        options.setRemoveDeadCode(true);
        options.setExtractPrototypeMemberDeclarations(ExtractPrototypeMemberDeclarationsMode.USE_IIFE);
        options.setRemoveUnusedPrototypePropertiesInExterns(true);
        options.setRemoveUnusedClassProperties(true);
        options.setRemoveUnusedVariables(Reach.ALL);
        options.setCollapseAnonymousFunctions(true);
        options.setCollapseVariableDeclarations(true);
        options.setAliasAllStrings(true);
        options.setConvertToDottedProperties(true);
        options.setRewriteFunctionExpressions(true);

        // Renaming
        options.setVariableRenaming(VariableRenamingPolicy.ALL);
        options.setPropertyRenaming(PropertyRenamingPolicy.ALL_UNQUOTED);
        options.setLabelRenaming(true);
        options.setGeneratePseudoNames(true);
        options.setShadowVariables(true);
        options.setPreferStableNames(true);
        options.setCollapsePropertiesLevel(PropertyCollapseLevel.ALL);
        options.setCollapseObjectLiterals(true);
        options.setDevirtualizeMethods(true);
        options.setAnonymousFunctionNaming(AnonymousFunctionNamingPolicy.MAPPED);
        return options;
    }

    private static void readWithPhax(final File file) throws InvalidInput {
        final CascadingStyleSheet aCSS = CSSReader.readFromFile (file, UTF_8, CSS30);
        if (aCSS == null) throw new InvalidInput("Couldn't parse CSS");

        for (final ICSSTopLevelRule rule : aCSS.getAllRules()) {
            if (match(rule, "html")) {
                used.add(rule);
            }
        }

        for (final ICSSTopLevelRule r : aCSS.getAllRules()) {
            if (!used.contains(r)) aCSS.removeRule(r);
        }

        final CSSWriter aWriter = new CSSWriter(new CSSWriterSettings(CSS30, false));
        System.out.println(aWriter.getCSSAsString(aCSS));

    }

    private static boolean match(final ICSSTopLevelRule top, final String tag) {
        if (!(top instanceof CSSStyleRule)) return true;
        if (used.contains(top)) return false;

        final CSSStyleRule rule = (CSSStyleRule) top;
        for (final CSSSelector s : rule.getAllSelectors()) {
            for (final ICSSSelectorMember m : s.getAllMembers()) {
                if (m instanceof CSSSelectorAttribute)
                    System.out.println(m.getClass());
                if (m instanceof CSSSelectorSimpleMember) {
                    System.out.println(((CSSSelectorSimpleMember) m).getValue());
                }
            }
        }
        return true;
    }
*/

/*
    private static void readWithCSSOMParser(final File file) throws IOException {
        final InputSource source = new InputSource(new FileReader(file));

        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
        CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

        final CSSRuleList rules = sheet.getCssRules();
        for (int i = 0; i < rules.getLength(); i++) {
            final CSSRule rule = rules.item(i);
            if (rule instanceof CSSStyleRule && !isUsed((CSSStyleRule) rule)) continue;

            System.out.println(rule.getCssText());
        }
    }
*/

/*
    private static boolean isUsed(final CSSStyleRule rule) {
        for (final SelectorMember m : toSelectorMembers(rule.getSelectorText())) {
            if (isUsed(m))
                return true;
        }
        return false;
    }
*/
/*
    private static boolean isUsed(final SelectorMember m) {
        switch (m.type) {
            case TYPE_CLASS: return classes.contains(m.name);
            case TYPE_ID: return ids.contains(m.name);
            case TYPE_TAG: return tags.contains(m.name);
            default: return true;
        }
    }

    private static List<SelectorMember> toSelectorMembers(final String selector) {
        final List<SelectorMember> ret = new ArrayList<>();
        for (final String v : selector.split("[,\\s]+")) {
            if (v != null && !v.isEmpty())
                ret.add(new SelectorMember(v));
        }
        return ret;
    }
*/

    private static final int
            TYPE_CLASS = 0,
            TYPE_ID = 1,
            TYPE_TAG = 2;

    private static class SelectorMember {
        private final int type;
        private final String name;

        private SelectorMember(final String text) {
            if (text.startsWith(".")) {
                type = TYPE_CLASS;
                name = text.substring(1);
            } else if (text.startsWith("#")) {
                type = TYPE_ID;
                name = text.substring(1);
            } else if (Character.isLetter(text.charAt(0))) {
                type = TYPE_TAG;
                name = text;
            } else {
                throw new IllegalArgumentException(String.format("Could not recognize selector: '%s'", text));
            }
        }
    }
}
