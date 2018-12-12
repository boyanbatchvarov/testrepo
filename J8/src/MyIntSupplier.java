import java.util.function.IntSupplier;

public class MyIntSupplier implements IntSupplier{
	private int counter = 0;
	
	public MyIntSupplier (int initial) {
		counter = initial;
	}

	@Override
	public int getAsInt() {
		return counter++;
	}
	
}
