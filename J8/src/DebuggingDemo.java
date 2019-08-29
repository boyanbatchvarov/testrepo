import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class DebuggingDemo extends TestBase {
	private List<Foo> foos = new ArrayList<>();

	@Before
	public void setUp() {
		runTracked(() -> {
			List<Foo> tempFoos = FooFactory.generateFoos(10);
			foos = Collections.unmodifiableList(tempFoos);
		});
	}

	/* Unchecked exceptions */

	// Debug order: terminal operation first, intermediate later (or never)
	@SuppressWarnings("unused")
	@Test
	public void debugMe() {
		final String outer = "I am out";

		// @formatter:off
		Set<Foo> set = foos.stream().unordered().
				filter(f -> 
					f.getName().startsWith("Foo")
				).
				filter(fo -> {
					String temp = fo.getName() + outer;
					return fo.getSalary() > 5;
				}).
				filter(foo -> foo.getSalary() > 7).
				collect(Collectors.toSet());
		 
		// @formatter:on

		foos.stream().unordered().filter(f -> f.getName().startsWith("Foo")).filter(foo -> foo.getSalary() > 5).collect(Collectors.toSet());

		set.forEach(f -> System.out.println(f));
	}

	// Use exception breakpoint
	@Test
	public void divideByInt0() {
		Integer newCarPrice = 350;
		foos.stream().forEach(f -> System.out.println(f.toString() + "; Months: " + newCarPrice / f.getSalary().intValue()));
	}
}
