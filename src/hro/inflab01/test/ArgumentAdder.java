package hro.inflab01.test;

public class ArgumentAdder {
	public static void main(String[] args) {
		if(args.length < 3) {
			System.err.println("Usage: argumentadder [int x] [int y] [boolean exitcode]");
			System.exit(123);
		}
		try {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			boolean exit = Boolean.parseBoolean(args[2]);
			System.out.println(x + y);
			if(exit) {
				System.exit(x + y);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(123);
		}
	}
}
