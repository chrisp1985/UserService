package com.chrisp1985.UserService.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserServiceMetrics extends AbstractMetrics {

    private static final String METRIC_NAME = "user_service";
    public UserServiceMetrics(MeterRegistry meterRegistry) {
        super(meterRegistry);
    }

    public void recordSuccess() {
        recordCounter(METRIC_NAME, tags());
    }

    public Double getRecordCount() {
        return meterRegistry.counter(METRIC_NAME, tags()).count();
    }

    private String[] tags() {
        return new String[]{RESULT_TAG, Result.SUCCESS.name(), FAILURE_REASON_TAG, NO_REASON};
    }
}
