package mutate4java;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void doesNotExitWhenCodeIsZero() {
        AtomicInteger exit = new AtomicInteger(-1);

        Main.exitIfNeeded(0, exit::set);

        assertEquals(-1, exit.get());
    }

    @Test
    void exitsWhenCodeIsNonZero() {
        AtomicInteger exit = new AtomicInteger(-1);

        Main.exitIfNeeded(3, exit::set);

        assertEquals(3, exit.get());
    }
}
