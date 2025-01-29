package mr.demonid.service.order.services;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * Класс для замера времени выполнения функций и процедур.
 */
@Service
public class MicrometerService {

    private final Timer timer;

    public MicrometerService(@Value("${custom.endpoint.name}") String endpointName, MeterRegistry registry) {
        System.out.println("-- create endpoint: " + endpointName);
        this.timer = Timer.builder(endpointName)            // имя эндпоинта
                .description("Timer for meter speed of Order-service")
                .tags("service", "example")
                .register(registry);
    }

    /**
     * Замер времени для методов возвращающих значение (функции).
     */
    public <T> T perform(Callable<T> task) throws RuntimeException{
        return timer.record( () -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException("Error executing task", e);
            }
        });
    }

    /**
     * Замер времени для методов типа void (процедуры).
     */
    public void perform(Runnable task) {
        timer.record(task);
    }

}
