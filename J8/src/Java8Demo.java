import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class Java8Demo extends TestBase {

	private List<Foo> foos = new ArrayList<>();

	@Before
	public void setUp() {
		runTrackedAction(() -> {
			List<Foo> tempFoos = FooFactory.generateFoos(100);
			foos = Collections.unmodifiableList(tempFoos);
		});
	}

	@Test
	public void printSalaries() {
		runTrackedAction(() -> foos.stream().forEach(f -> System.out.println(f.toString())));
	}

	@Test
	public void printSalariesParallel() {
		runTrackedAction(() -> foos.parallelStream().forEach(f -> System.out.println(f.toString())));
	}

	@Test
	public void getSalaries() {
		runTrackedAction(() -> foos.stream().map(f -> f.getSalary()).collect(Collectors.toList()));
	}

	@Test
	public void getSalariesParallel() {
		runTrackedAction(() -> foos.parallelStream().map(f -> f.getSalary()).collect(Collectors.toList()));
	}

	@Test
	public void getSalariesSum() {
		runTrackedAction(() -> foos.stream().map(f -> f.getSalary()).reduce((a, b) -> a + b));
	}

	@Test
	public void getSalariesSumParallel() {
		runTrackedAction(() -> foos.parallelStream().map(f -> f.getSalary()).reduce((a, b) -> a + b));
	}

	@Test
	public void getSalariesSumWithResult() {
		runTrackedFunction(() -> {
			return foos.stream().map(f -> f.getSalary()).reduce((a, b) -> a + b).get();
		});
	}

	@Test
	public void getSalariesSumWithResultParallel() {
		runTrackedFunction(() -> {
			return foos.parallelStream().map(f -> f.getSalary()).reduce((a, b) -> a + b).get();
		});
	}

	@Test
	public void getFirstBigSalaryWithResult() {
		runTrackedFunction(() -> {
			return foos.stream().map(f -> f.getSalary()).filter(s -> s > MILLION).findFirst().get();
		});
	}

	@Test
	public void getFirstBigSalaryWithResultParallel() {
		runTrackedFunction(() -> {
			return foos.parallelStream().map(f -> f.getSalary()).filter(s -> s > MILLION).findFirst().get();
		});
	}

	@Test
	public void getAnyBigSalaryWithResult() {
		runTrackedFunction(() -> {
			return foos.stream().map(f -> f.getSalary()).filter(s -> s > 50).findAny().orElse(-1.);
		});
	}

	@Test
	public void getAnyBigSalaryWithResultParallel() {
		runTrackedFunction(() -> {
			return foos.parallelStream().map(f -> f.getSalary()).filter(s -> s > 50).findAny().orElse(-1.);
		});
	}

	// TODO: @BB move to tests
	// // endlessInts();
	// // numbers();
	//
	// List<Foo> foos = FooFactory.generateFoos(1000000);
	//
	// Date start = new Date();
	//
	// List<Double> sals2 = foos.stream().map(f -> f.getSalary()).collect(Collectors.toList());
	//
	// Date mid = new Date();
	// System.out.println(mid.getTime() - start.getTime());
	// List<Double> sals = foos.parallelStream().map(f -> f.getSalary()).collect(Collectors.toList());
	//
	// // getSalariesLoop(foos);
	// // getTotalStreamsReduce(sals);
	// // getTotalStreamsReduceParallel(sals);
	// // getSalariesStreamsReduce(foos);
	// // getSalariesStreamsReduceParallel(foos);
	//
	// // System.out.println(System.getProperty("java.runtime.version"));
	//
	// Date end = new Date();
	// System.out.println(end.getTime() - mid.getTime());
	// }

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
}
