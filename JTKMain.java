import java.util.Random;



public class JTKMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int j;
		Random flushot = new Random();
		flushot.setSeed(System.currentTimeMillis());
		j = flushot.nextInt() % 100;
		System.out.print(Math.abs(j));

	}

}
