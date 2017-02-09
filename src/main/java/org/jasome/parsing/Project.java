package org.jasome.parsing;

import java.util.Set;

public class Project extends TreeNode {
    public Project() {
        super("root");
    }

    @SuppressWarnings("unchecked")
    public Set<Package> getPackages() {
        return (Set<Package>)(Set<?>)getChildren();
    }

    public void addPackage(Package aPackage) {
        addChild(aPackage);
    }

    @Override
    public String toString() {
        return "Project";
    }
}