import java.util.ArrayList;
import java.util.List;

public class FooFactory {
	public static List<Foo> generateFoos(int count) {
		List<Foo> foos = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (i % 2 == 0) {
				foos.add(new Foo("Foo" + i, 1.0 * i));
			} else {
				foos.add(new Foo("Bar" + i, 1.0 * i));
			}
		}

		return foos;
	}
}
