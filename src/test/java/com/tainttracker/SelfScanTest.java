package com.tainttracker;

import com.tainttracker.analyzer.BytecodeWalker;
import com.tainttracker.scanner.ProjectScanner;
import com.tainttracker.scanner.ScanContext;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SelfScanTest {

    @Test
    public void testSelfScan() {
        // Point to current project root
        File root = Paths.get(".").toAbsolutePath().normalize().toFile();
        System.out.println("Scanning: " + root);

        ProjectScanner scanner = new ProjectScanner();
        ScanContext context = scanner.scan(root);

        assertFalse(context.getJavaFiles().isEmpty(), "Should find Java files");
        assertFalse(context.getDependencies().isEmpty(), "Should find dependencies (mock)");

        // If we have compiled classes in target/classes, we can test analyzer
        File classesDir = new File(root, "target/classes");
        if (classesDir.exists()) {
            System.out.println("Analyzing classes in " + classesDir);
            ScanContext classContext = scanner.scan(classesDir);
            assertFalse(classContext.getClassFiles().isEmpty(), "Should find class files in target/classes");

            BytecodeWalker analyzer = new BytecodeWalker();
            for (File classFile : classContext.getClassFiles()) {
                List<BytecodeWalker.VulnerabilityFitting> vulns = analyzer.analyzeProperties(classFile);
                if (!vulns.isEmpty()) {
                    System.out.println("Found vulnerabilities: " + vulns);
                }
            }
        } else {
            System.out.println("Skipping bytecode analysis test as target/classes does not exist yet.");
        }
    }
}
