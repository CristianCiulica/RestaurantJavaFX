package mip.restaurantfx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class FxExecutors {

    private static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
            new DaemonNamedThreadFactory("db")
    );

    private FxExecutors() {
    }

    public static ExecutorService db() {
        return DB_EXECUTOR;
    }

    public static void shutdown() {
        DB_EXECUTOR.shutdownNow();
    }

    private static final class DaemonNamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger idx = new AtomicInteger(1);

        private DaemonNamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("RestaurantFX-" + prefix + "-" + idx.getAndIncrement());
            return t;
        }
    }
}

