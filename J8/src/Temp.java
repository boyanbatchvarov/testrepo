import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class Temp extends TestBase {

	private List<Foo> foos = new ArrayList<>();

	@Before
	public void setUp() {
		runTracked(() -> {
			List<Foo> tempFoos = FooFactory.generateFoos(100);
			foos = Collections.unmodifiableList(tempFoos);
		});
	}

	@Test
	public void printSalaries() {
		foos.stream().forEach(f -> System.out.println(f.toString()));
	}

	@Test
	public void getBigSalaries() {
		foos.stream().filter(f -> f.getSalary() > 10000).distinct();
	}

	@Test
	public void getBigEarners() {
		foos.stream().filter(f -> f.getSalary() > 10000).map(f -> f.getName()).sorted().limit(10).collect(Collectors.toList());
	}

	@Test
	public void doubleSalaries() {
		foos.stream().forEach(f -> f.setSalary(f.getSalary() * 2));
		printSalaries();
	}

	@Test
	public void printSalariesTracked() {
		runTracked(() -> foos.stream().forEach(f -> System.out.println(f.toString())));
	}

	@Test
	public void printSalariesParallel() {
		runTracked(() -> foos.parallelStream().forEach(f -> System.out.println(f.toString())));
	}

	@Test
	public void getSalaries() {
		runTracked(() -> foos.stream().map(f -> f.getSalary()).collect(Collectors.toList()));
	}

	@Test
	public void getSalariesParallel() {
		runTracked(() -> foos.parallelStream().map(f -> f.getSalary()).collect(Collectors.toList()));
	}

	@Test
	public void getSalariesSum() {
		runTracked(() -> foos.stream().map(f -> f.getSalary()).reduce((a, b) -> a + b));
	}

	@Test
	public void getSalariesSumParallel() {
		runTracked(() -> foos.parallelStream().map(f -> f.getSalary()).reduce((a, b) -> a + b));
	}

	@Test
	public void getSalariesSumWithResult() {
		runTracked(() -> {
			return foos.stream().map(f -> f.getSalary()).reduce((a, b) -> a + b).get();
		});
	}

	@Test
	public void getSalariesSumWithResultParallel() {
		runTracked(() -> {
			return foos.parallelStream().map(f -> f.getSalary()).reduce((a, b) -> a + b).get();
		});
	}

	@Test
	public void getFirstBigSalaryWithResult() {
		runTracked(() -> {
			return foos.stream().map(f -> f.getSalary()).filter(s -> s > MILLION).findFirst().get();
		});
	}

	@Test
	public void getFirstBigSalaryWithResultParallel() {
		runTracked(() -> {
			return foos.parallelStream().map(f -> f.getSalary()).filter(s -> s > MILLION).findFirst().get();
		});
	}

	@Test
	public void getAnyBigSalaryWithResult() {
		runTracked(() -> {
			return foos.stream().map(f -> f.getSalary()).filter(s -> s > 50).findAny().orElse(-1.);
		});
	}

	@Test
	public void getAnyBigSalaryWithResultParallel() {
		runTracked(() -> {
			return foos.parallelStream().map(f -> f.getSalary()).filter(s -> s > 50).findAny().orElse(-1.);
		});
	}

	private static void getTotalStreamsReduce(List<Double> sals) {
		Optional<Double> total = sals.stream().reduce((a, b) -> a + b);
		System.out.println(total.get());
	}

	private static void getTotalStreamsReduceParallel(List<Double> sals) {
		Optional<Double> total = sals.parallelStream().reduce((a, b) -> a + b);
		System.out.println(total.get());
	}

	private static void getSalariesStreamsReduce(List<Foo> foos) {
		Optional<Foo> cumSalaries = foos.stream().reduce((a, b) -> new Foo("all", a.getSalary() + b.getSalary()));
		System.out.println(cumSalaries.get().getSalary());
	}

	private static void getTotalSalaryWithReduce(List<Foo> foos) {
		Optional<Double> total = foos.stream().map(f -> f.getSalary()).reduce((left, right) -> left + right);
		System.out.println(total.get());
	}

	// @BB this accumulator doesn't look right
	private static void getSalariesStreamsReduceParallel(List<Foo> foos) {
		Optional<Foo> cumSalaries = foos.parallelStream().reduce((a, b) -> new Foo("all", a.getSalary() + b.getSalary()));
		System.out.println(cumSalaries.get().getSalary());
	}

	private static void getSalariesLoop(List<Foo> foos) {
		Double salary = 0.;
		for (Foo foo : foos) {
			salary += foo.getSalary();
		}

		System.out.println(salary);
	}

	private static void numbers() {
		MyIntSupplier intSupplier = new MyIntSupplier(0);
		IntStream numbers = IntStream.generate(intSupplier);

		numbers.limit(100).skip(10).filter(i -> i % 2 == 0).forEach(i -> System.out.println(i));

		// Optional<Integer> first = ones.findFirst();
		// System.out.println(first.isPresent() ? first.get() : "no first el");
	}

	private static void endlessInts() {
		Stream<Integer> ones = Stream.generate(() -> 1);
		ones.limit(10).forEach(i -> System.out.println(i));
	}

	@Test
	public void testParallelCommonPool() {
		// System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "10");

		runTracked(() -> {
			foos.parallelStream().map(f -> runTrackedFunction(foo -> foo.getSalary(), f)).forEach(f -> System.out.println(f));
		});
	}

	@Test
	public void testParallelCustomPool() throws Exception {
		runInParallelAndWait(
				() -> foos.parallelStream().map(foo -> runTrackedFunction(f -> f.getSalary(), foo)).forEach(s -> System.out.println(s)));
	}

	public static void runInParallelAndWait(Runnable runnable) throws Exception {
		ForkJoinPool pool = null;
		final int parallelism = 100;

		try {
			pool = new ForkJoinPool(parallelism);
			pool.submit(runnable).get(Long.MAX_VALUE, TimeUnit.HOURS);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		} finally {
			if (pool != null) {
				pool.shutdown();
			}
		}
	}
}
