package com.github.mikrop.quickfix;

import java.io.File;
import java.io.FilenameFilter;

class ClassnameFilter implements FilenameFilter {
    private String classname;
    private String classnameToMatch;

    public ClassnameFilter(final String javaFileName) {
        this.classname = javaFileName.replaceAll(".java", ".class");
        String[] splitted = javaFileName.split(".java");
        this.classnameToMatch = (splitted[0] + "$");
    }

    public boolean accept(final File file, final String string) {
        return (string.equalsIgnoreCase(this.classname)) || (string.contains(this.classnameToMatch));
    }
}
