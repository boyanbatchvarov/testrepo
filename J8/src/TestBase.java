import java.text.MessageFormat;
import java.util.Date;

public class TestBase {
	public static final int MILLION = 1000 * 1000;

	public interface Action {
		void run();
	}

	public interface Function<T> {
		T run();
	}

	public static String getCallingMethodName() {
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	public static void runTrackedAction(Action runner) {
		Date start = new Date();

		runner.run();

		Date end = new Date();

		String callerMethodName = getCallingMethodName();
		System.out.println(MessageFormat.format("{0}: {1}ms", callerMethodName, end.getTime() - start.getTime()));
	}

	public static <T> void runTrackedFunction(Function<T> func) {
		Date start = new Date();

		T result = func.run();

		Date end = new Date();

		String callerMethodName = getCallingMethodName();
		System.out.println(MessageFormat.format("{0}: {1}ms", callerMethodName, end.getTime() - start.getTime()));
		System.out.println(MessageFormat.format("	Result: {0}", result));
	}
}
