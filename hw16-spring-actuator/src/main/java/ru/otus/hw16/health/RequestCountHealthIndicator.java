package ru.otus.hw16.health;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestCountHealthIndicator implements HealthIndicator {
    private final static Double ONE_MINUTE_RATE_THRESHOLD = 0.01;
    private final RequestCounter requestCounter;


    @Override
    public Health health() {
        val fifteenMinuteRate = requestCounter.getFifteenMinuteRate();
        val oneMinuteRate = requestCounter.getOneMinuteRate();
        if (oneMinuteRate < ONE_MINUTE_RATE_THRESHOLD)
            return Health.down().withDetails(Map.of(
                    "fifteenMinuteRate", fifteenMinuteRate,
                    "oneMinuteRate", oneMinuteRate,
                    "message", "The number of requests is less than usual"
            )).build();

        return Health.up().withDetails(Map.of(
                "fifteenMinuteRate", fifteenMinuteRate,
                "oneMinuteRate", oneMinuteRate,
                "message", "The number of requests as usual"
        )).build();

    }
}
