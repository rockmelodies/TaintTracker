package com.tainttracker.analyzer;

public record SinkDefinition(String className, String methodName, String descriptor, String description,
        int riskLevel) {
    public static final int RISK_HIGH = 3;
    public static final int RISK_MEDIUM = 2;
    public static final int RISK_LOW = 1;
}
