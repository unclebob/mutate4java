package mutate4java;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

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
