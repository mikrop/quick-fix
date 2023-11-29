package com.github.mikrop.quickfix;

import java.util.regex.Pattern;

public class ModuleWorker {

    private static final String SOURCE_ROOT_REGEX = "src/[main|test]+/[java|resources]+";
    public static final Pattern SOURCE_ROOT_PATTERN = Pattern.compile(SOURCE_ROOT_REGEX);

    private ModuleWorker() {
    }

    public static String replaceSourceRoot(final String relativePath) {
        return relativePath.replaceFirst(SOURCE_ROOT_REGEX + '/', "");
    }
}
