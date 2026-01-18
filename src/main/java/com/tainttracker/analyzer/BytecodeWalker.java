package com.tainttracker.analyzer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BytecodeWalker {
    private static final Logger logger = LoggerFactory.getLogger(BytecodeWalker.class);
    private final SinkDetector sinkDetector;

    public BytecodeWalker() {
        this.sinkDetector = new SinkDetector();
    }

    public List<VulnerabilityFitting> analyzeProperties(File classFile) {
        List<VulnerabilityFitting> vulnerabilities = new ArrayList<>();
        try (InputStream is = new FileInputStream(classFile)) {
            ClassReader reader = new ClassReader(is);
            reader.accept(new AnalysisVisitor(vulnerabilities), ClassReader.SKIP_DEBUG);
        } catch (IOException e) {
            logger.error("Failed to analyze class: " + classFile.getName(), e);
        }
        return vulnerabilities;
    }

    private class AnalysisVisitor extends ClassVisitor {
        private final List<VulnerabilityFitting> vulnerabilities;
        private String className;

        public AnalysisVisitor(List<VulnerabilityFitting> vulnerabilities) {
            super(Opcodes.ASM9);
            this.vulnerabilities = vulnerabilities;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName,
                String[] interfaces) {
            this.className = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                String[] exceptions) {
            return new MethodAnalyzer(vulnerabilities, className, name, descriptor);
        }
    }

    private class MethodAnalyzer extends MethodVisitor {
        private final List<VulnerabilityFitting> vulnerabilities;
        private final String className;
        private final String methodName;

        public MethodAnalyzer(List<VulnerabilityFitting> vulnerabilities, String className, String methodName,
                String descriptor) {
            super(Opcodes.ASM9);
            this.vulnerabilities = vulnerabilities;
            this.className = className;
            this.methodName = methodName;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            SinkDefinition sink = sinkDetector.isSink(owner, name, descriptor);
            if (sink != null) {
                VulnerabilityFitting vuln = new VulnerabilityFitting(
                        className,
                        methodName,
                        sink,
                        "Detected potential " + sink.description());
                vulnerabilities.add(vuln);
                logger.warn("Found vulnerability: {} in {}.{}", sink.description(), className, methodName);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    // Simple container for found vulnerabilities
    public record VulnerabilityFitting(String sourceClass, String sourceMethod, SinkDefinition sink, String message) {
    }
}
