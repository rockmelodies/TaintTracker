package com.tainttracker.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScanContext {
    private File projectRoot;
    private List<File> javaFiles = new ArrayList<>();
    private List<File> classFiles = new ArrayList<>();
    private List<File> jarFiles = new ArrayList<>();
    private List<File> configFiles = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();

    public ScanContext(File projectRoot) {
        this.projectRoot = projectRoot;
    }

    public void addJavaFile(File file) {
        javaFiles.add(file);
    }

    public void addClassFile(File file) {
        classFiles.add(file);
    }

    public void addJarFile(File file) {
        jarFiles.add(file);
    }

    public void addConfigFile(File file) {
        configFiles.add(file);
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    public List<File> getJavaFiles() {
        return javaFiles;
    }

    public List<File> getClassFiles() {
        return classFiles;
    }

    public List<File> getJarFiles() {
        return jarFiles;
    }

    public List<File> getConfigFiles() {
        return configFiles;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public File getProjectRoot() {
        return projectRoot;
    }
}
