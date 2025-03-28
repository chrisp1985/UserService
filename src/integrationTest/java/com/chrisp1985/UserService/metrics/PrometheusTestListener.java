package com.chrisp1985.UserService.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PrometheusTestListener implements TestExecutionListener {

    private static final Gauge testsPassed = Gauge.build()
            .name("junit_tests_passed")
            .help("Number of JUnit tests that passed")
            .register();

    private static final Gauge testsFailed = Gauge.build()
            .name("junit_tests_failed")
            .help("Number of JUnit tests that failed")
            .register();

    private static final CollectorRegistry registry = new CollectorRegistry();
    private static final PushGateway pushGateway = new PushGateway("localhost:9091"); // Change to your Pushgateway URL

    static {
        registry.register(testsPassed);
        registry.register(testsFailed);
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest()) {
            switch (testExecutionResult.getStatus()) {
                case SUCCESSFUL -> testsPassed.inc();
                case FAILED -> testsFailed.inc();
            }
        }
    }

    @Override
    public void testPlanExecutionFinished(org.junit.platform.launcher.TestPlan testPlan) {
        try {
            pushGateway.pushAdd(registry, "junit_test_results");
            System.out.println("✅ Test metrics pushed to Prometheus Pushgateway");
        } catch (IOException e) {
            System.err.println("❌ Failed to push metrics to Prometheus: " + e.getMessage());
        }
    }
}