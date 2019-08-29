import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class ExceptionHandlingDemo extends TestBase {
	private List<Foo> foos = new ArrayList<>();

	@Before
	public void setUp() {
		runTracked(() -> {
			List<Foo> tempFoos = FooFactory.generateFoos(10);
			foos = Collections.unmodifiableList(tempFoos);
		});
	}

	/* Unchecked exceptions */

	@Test
	public void divideBy0() {
		Double newCarPrice = 350.;
		foos.stream().forEach(f -> System.out.println(f.toString() + "; Months: " + newCarPrice / f.getSalary()));
	}

	@Test
	public void divideByInt0() {
		Integer newCarPrice = 350;
		foos.stream().forEach(f -> System.out.println(f.toString() + "; Months: " + newCarPrice / f.getSalary().intValue()));
	}

	@Test
	public void uncheckedExceptionHandled() {
		Integer newCarPrice = 350;
		foos.stream().forEach(f -> {
			try {
				System.out.println(MessageFormat.format("{0}: {1} Months", f.toString(), newCarPrice / f.getSalary().intValue()));
			} catch (RuntimeException e) {
				System.out.println(f.toString() + ": will take forever!");
			}
		});
	}

	/* Checked exceptions */

	@Test
	public void checkedExceptionHandleInline() {
		String names = foos.stream().map(f -> f.getName()).map(s -> {
			try {
				return URLEncoder.encode(s, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "hm?";
			}
		}).collect(Collectors.joining(","));

		System.out.println(names);
	}

	@Test
	public void checkedExceptionHandleDedicatedMethod() {
		foos.stream().map(f -> f.getName()).map(s -> encode(s)).collect(Collectors.joining(","));
	}

	private String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "hm?";
		}
	}

	@FunctionalInterface
	public interface FunctionWithException<T, R, E extends Exception> {
		R apply(T t) throws E;
	}

	private <T, R, E extends Exception> Function<T, R> safelyRun(FunctionWithException<T, R, E> f) {
		return arg -> {
			try {
				return f.apply(arg);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		};
	}

	@Test
	public void checkedExceptionHandleWithWrapper() {
		foos.stream().map(f -> f.getName()).map(safelyRun(s -> URLEncoder.encode(s, "UTF-8"))).collect(Collectors.joining(","));
	}
}
