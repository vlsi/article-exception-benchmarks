package net.shipilev.perf.exceptions;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class BasicBench {

    LilException staticException;

    int source = 42;

    @Setup(Level.Iteration)
    public void setup() {
        staticException = new LilException(source);
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    public int doSomething() {
        return source;
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    public int doSomething_Exception() throws LilException {
        throw new LilException(source);
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    public int doSomething_Exception_Static() throws LilException {
        throw staticException;
    }

    @Benchmark
    public int plain() {
        return doSomething();
    }

    @Benchmark
    public int dynamicException_() {
        try {
            return doSomething_Exception();
        } catch (LilException e) {
            return source;
        }
    }

    @Benchmark
    public Class<?> stackWalker_getCallerClass() {
        return StackWalker.getInstance(RETAIN_CLASS_REFERENCE).getCallerClass();
    }

    @Benchmark
    public Integer stackWalker_find1() {
        return StackWalker.getInstance().walk(s -> s.findFirst().get().getLineNumber());
    }

    @Benchmark
    public StackTraceElement dynamicExceptionn_find1() {
        try {
            doSomething_Exception();
            return null;
        } catch (LilException e) {
            return e.getStackTrace()[0];
        }
    }


    @Benchmark
    public Long stackWalker_count() {
        return StackWalker.getInstance().walk(Stream::count);
    }

    @Benchmark
    public int dynamicException_UsedData() {
        try {
            return doSomething_Exception();
        } catch (LilException e) {
            return e.getMetadata();
        }
    }


    @Benchmark
    public int dynamicException_UsedStack() {
        try {
            return doSomething_Exception();
        } catch (LilException e) {
            return e.getStackTrace().length;
        }
    }

    @Benchmark
    @Fork(jvmArgs = "-XX:-StackTraceInThrowable")
    public int dynamicException_NoStack() {
        try {
            return doSomething_Exception();
        } catch (LilException e) {
            return source;
        }
    }

    @Benchmark
    @Fork(jvmArgs = "-XX:-StackTraceInThrowable")
    public int dynamicException_NoStack_UsedData() {
        try {
            return doSomething_Exception();
        } catch (LilException e) {
            return e.getMetadata();
        }
    }

    @Benchmark
    @Fork(jvmArgs = "-XX:-StackTraceInThrowable")
    public int dynamicException_NoStack_UsedStack() {
        try {
            return doSomething_Exception();
        } catch (LilException e) {
            return e.getStackTrace().length;
        }
    }

    @Benchmark
    public int staticException() {
        try {
            return doSomething_Exception_Static();
        } catch (LilException e) {
            return source;
        }
    }

    @Benchmark
    public int staticException_UsedData() {
        try {
            return doSomething_Exception_Static();
        } catch (LilException e) {
            return e.getMetadata();
        }
    }

    @Benchmark
    public int staticException_UsedStack() {
        try {
            return doSomething_Exception_Static();
        } catch (LilException e) {
            return e.getStackTrace().length;
        }
    }

    @Benchmark
    @Fork(jvmArgs = "-XX:-StackTraceInThrowable")
    public int staticException_NoStack() {
        try {
            return doSomething_Exception_Static();
        } catch (LilException e) {
            return source;
        }
    }

    @Benchmark
    @Fork(jvmArgs = "-XX:-StackTraceInThrowable")
    public int staticException_NoStack_UsedData() {
        try {
            return doSomething_Exception_Static();
        } catch (LilException e) {
            return e.getMetadata();
        }
    }

    @Benchmark
    @Fork(jvmArgs = "-XX:-StackTraceInThrowable")
    public int staticException_NoStack_UsedStack() {
        try {
            return doSomething_Exception_Static();
        } catch (LilException e) {
            return e.getStackTrace().length;
        }
    }

}
