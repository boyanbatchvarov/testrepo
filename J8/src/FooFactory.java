import java.util.ArrayList;
import java.util.List;

public class FooFactory {
	public static List<Foo> generateFoos(int count) {
		List<Foo> foos = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			foos.add(new Foo("name " + i, 1.0 * i));
		}

		return foos;
	}
}
