package org.jasome.input;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;

public class StringScanner extends Scanner {

    private String absolutePath;
    private Collection<Pair<String, Map<String, String>>> sourceCodeWithAttributes;

    public StringScanner(Collection<Pair<String, Map<String, String>>> sourceCodeWithAttributes, String absolutePath) {
        this.sourceCodeWithAttributes = sourceCodeWithAttributes;
        this.absolutePath = absolutePath;
    }

    public Project scan() {
        Project project = doScan(sourceCodeWithAttributes, absolutePath);
        project.addAttribute("sourceDir", absolutePath);
        return project;
    }
}
