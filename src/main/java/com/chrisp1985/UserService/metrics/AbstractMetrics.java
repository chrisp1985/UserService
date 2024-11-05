package com.chrisp1985.UserService.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AbstractMetrics {

    public static final String FAILURE_REASON_TAG = "reason";
    public static final String NO_REASON = "";
    public static final String RESULT_TAG = "result";
    public enum Result {FAILURE, SUCCESS}

    protected final MeterRegistry meterRegistry;

    protected final void recordCounter(String name, String... tags) {
        try {
            meterRegistry.counter(name, tags).increment();
        } catch (Exception e) {
            //Do not bomb out process if metrics fail
            log.warn("Error writing metric to Prometheus", e);
        }
    }
}
