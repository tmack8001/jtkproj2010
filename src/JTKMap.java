
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.*;

public class JTKMap {

	public byte[] workspace;

	public static void main(String args[]) {
		new JTKMap();
	}

	public JTKMap() {
		File f = new File("3large.raw");
		workspace = new byte[1600*500];
		try { 
			DataInputStream in = new DataInputStream(
				new FileInputStream(f));
			for(int i=0;i<1600*500;i++) {
				//workspace = (in.readByte < 0);
				workspace[i] = in.readByte();
			}
		} catch(IOException e) {
			System.out.println("Error reading map: " + e);
		}
	}

	public byte at(int x, int y) {
		return workspace[1600*x+y];
	}

}
