import java.text.MessageFormat;

public class Foo {
	public Foo(String name, Double salary) {
		super();
		this.name = name;
		this.salary = salary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	private String name;
	private Double salary;

	@Override
	public String toString() {
		return MessageFormat.format("Name: {0}, salary: {1}", name, salary);
	}
}
