import java.util.Random;



public class JTKMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int j;
		Random flushot = new Random();
		flushot.setSeed(System.currentTimeMillis());
		for(int i = 0; i < 50; i++){
			j = flushot.nextInt() % 100;
			System.out.println(Math.abs(j));
		}
	}

}
