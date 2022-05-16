package ru.otus.hw16.health;


import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class RequestCounter {
    private final Meter requests;

    public RequestCounter() {
        val metrics = new MetricRegistry();
        requests = metrics.meter("requests");
    }

    public void mark() {
        requests.mark();
    }

    public Double getFifteenMinuteRate() {
        return requests.getFifteenMinuteRate();
    }

    public Double getOneMinuteRate() {
        return requests.getOneMinuteRate();
    }

    public Long getCount() {
        return requests.getCount();

    }
}
