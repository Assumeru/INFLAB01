package hro.inflab01.test;

public class RandomCrash implements Runnable {
	private final int mult;

	public RandomCrash(String[] args) {
		mult = getMult(args);
	}

	private int getMult(String[] args) {
		if(args.length > 0) {
			try {
				return Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return 10;
	}

	private int getRandomValue() {
		return (int) (Math.random() * mult);
	}

	@Override
	public void run() {
		while(true) {
			System.out.println(100 / getRandomValue());
		}
	}

	public static void main(String[] args) {
		new RandomCrash(args).run();
	}
}
