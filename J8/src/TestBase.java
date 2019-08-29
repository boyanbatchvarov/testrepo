import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class TestBase {
	public static final int MILLION = 1000 * 1000;

	// @FunctionalInterface
	// public interface TestAction {
	// void run();
	// }
	//
	// @FunctionalInterface
	// public interface TestFunction<T> {
	// T run();
	// }

	public static String getCallingMethodName() {
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	public static void runTracked(Runnable action) {
		String callerMethodName = getCallingMethodName();
		System.out.println(MessageFormat.format("Executing {0} on {1}", callerMethodName, Thread.currentThread()));
		Date start = new Date();

		action.run();

		Date end = new Date();

		System.out.println(MessageFormat.format("{0}: {1}ms", callerMethodName, end.getTime() - start.getTime()));
	}

	public static <T> T runTracked(Callable<T> callable) {
		String callerMethodName = getCallingMethodName();
		System.out.println(MessageFormat.format("Executing {0} on {1}", callerMethodName, Thread.currentThread()));

		Date start = new Date();

		T result = null;
		try {
			result = callable.call();
		} catch (Exception e) {
			System.out.println(MessageFormat.format("{0} execution failed", callerMethodName));
		}

		Date end = new Date();

		System.out.println(MessageFormat.format("{0}: {1}ms", callerMethodName, end.getTime() - start.getTime()));
		System.out.println(MessageFormat.format("	Result: {0}", result));

		return result;
	}

	public static <T, R> R runTrackedFunction(Function<T, R> func, T param) {
		String callerMethodName = getCallingMethodName();
		System.out.println(MessageFormat.format("Executing {0} on {1}", callerMethodName, Thread.currentThread()));

		Date start = new Date();

		R result = null;
		try {
			result = func.apply(param);
		} catch (Exception e) {
			System.out.println(MessageFormat.format("{0} execution failed", callerMethodName));
		}

		Date end = new Date();

		System.out.println(MessageFormat.format("{0}: {1}ms", callerMethodName, end.getTime() - start.getTime()));
		System.out.println(MessageFormat.format("	Result: {0}", result));

		return result;
	}
}
