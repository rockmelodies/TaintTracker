package com.tainttracker.analyzer;

import java.util.ArrayList;
import java.util.List;

public class SinkDetector {
    private final List<SinkDefinition> knownSinks;

    public SinkDetector() {
        knownSinks = new ArrayList<>();
        initializeSinks();
    }

    private void initializeSinks() {
        // Java Deserialization
        knownSinks.add(new SinkDefinition("java/io/ObjectInputStream", "readObject", "()Ljava/lang/Object;",
                "Java Deserialization Entry Point", SinkDefinition.RISK_HIGH));

        // XML Decoder
        knownSinks.add(new SinkDefinition("java/beans/XMLDecoder", "readObject", "()Ljava/lang/Object;",
                "XML Decoder Deserialization", SinkDefinition.RISK_HIGH));

        // Runtime Exec
        knownSinks.add(new SinkDefinition("java/lang/Runtime", "exec", "(Ljava/lang/String;)Ljava/lang/Process;",
                "Command Execution", SinkDefinition.RISK_HIGH));

        // ProcessBuilder
        knownSinks.add(new SinkDefinition("java/lang/ProcessBuilder", "start", "()Ljava/lang/Process;",
                "Command Execution via ProcessBuilder", SinkDefinition.RISK_HIGH));

        // JNDI Lookup
        knownSinks.add(new SinkDefinition("javax/naming/Context", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;",
                "JNDI Injection", SinkDefinition.RISK_HIGH));
        knownSinks.add(new SinkDefinition("javax/naming/InitialContext", "lookup",
                "(Ljava/lang/String;)Ljava/lang/Object;", "JNDI Injection", SinkDefinition.RISK_HIGH));
    }

    public SinkDefinition isSink(String owner, String name, String descriptor) {
        for (SinkDefinition sink : knownSinks) {
            if (sink.className().equals(owner) && sink.methodName().equals(name)) {
                // Descriptor check can be loose or strict. For now, checking name and owner is
                // often enough for review.
                // Strict check:
                // if (sink.descriptor().equals(descriptor)) return sink;
                return sink;
            }
        }
        return null;
    }
}
