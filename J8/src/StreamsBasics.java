import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StreamsBasics extends TestBase {
	private List<Foo> foos = new ArrayList<>();

	@Before
	public void setUp() {
		runTracked(() -> {
			List<Foo> tempFoos = FooFactory.generateFoos(10);
			foos = Collections.unmodifiableList(tempFoos);
		});
	}

	@Test
	public void printSalaries() {
		foos.stream().forEach(f -> System.out.println(f.toString()));
	}

	@Test
	public void doubleSalaries() {
		foos.stream().forEach(f -> f.setSalary(f.getSalary() * 2));
		printSalaries();
	}

}
