package com.tainttracker.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ProjectScanner {
    private static final Logger logger = LoggerFactory.getLogger(ProjectScanner.class);
    private final DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer();

    public ScanContext scan(File rootDir) {
        logger.info("Starting scan of project: {}", rootDir.getAbsolutePath());
        ScanContext context = new ScanContext(rootDir);

        try {
            Files.walkFileTree(rootDir.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.getFileName().toString();
                    File f = file.toFile();

                    if (fileName.endsWith(".java")) {
                        context.addJavaFile(f);
                    } else if (fileName.endsWith(".class")) {
                        context.addClassFile(f);
                    } else if (fileName.endsWith(".jar")) {
                        context.addJarFile(f);
                    } else if (isConfigFile(fileName)) {
                        context.addConfigFile(f);
                        if (fileName.equals("pom.xml")) {
                            dependencyAnalyzer.analyze(f, context);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.getFileName().toString().startsWith(".") || dir.getFileName().toString().equals("target")
                            || dir.getFileName().toString().equals("node_modules")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Error walking project tree", e);
        }

        logger.info("Scan complete. Found {} Java files, {} JARs", context.getJavaFiles().size(),
                context.getJarFiles().size());
        return context;
    }

    private boolean isConfigFile(String fileName) {
        return fileName.equals("pom.xml") ||
                fileName.equals("web.xml") ||
                fileName.endsWith(".yml") ||
                fileName.endsWith(".properties") ||
                fileName.endsWith(".xml");
    }
}
