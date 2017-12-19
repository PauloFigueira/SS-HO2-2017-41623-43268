import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Base64.getEncoder;

public class AccessController {

	//private static final String DIR = "/home/x3me/Desktop/5-1SEM/SegSoft/";
	private String dir;

	private static final byte[] keyValue = new
	 byte[]{'S','E','G','U','R','A','C','A','S','O','F', 'T','W','A','R','E'};
	private static final String ALGO = "AES";


	public AccessController(String dir) {
		this.dir=dir;
	}

	public void deleteUserFile(String user) throws IOException {
		Path path = Paths.get(dir + user + ".txt");
		Files.delete(path);
	}

	public List<Capability> getCapabilities(HttpServletRequest request) throws FileNotFoundException {
		HttpSession session = request.getSession();
		File file = new File(dir + session.getAttribute("USER").toString() + ".txt");
		Scanner input;
		input = new Scanner(file);
		List<Capability> caps = new ArrayList<Capability>();
		while (input.hasNextLine()) {
			caps.add(new Capability(input.nextLine()));
		}
		input.close();
		return caps;
	}

	public Capability makeKey(String resource, String operation, String expireTime) throws Exception {


		Path path = Paths.get(dir +"capabilities.txt");

		String newCap = resource + ',' + operation + ":";
		String keyCap = encrypt(resource + "-" + operation);
		Files.write(path, (newCap + keyCap + "\n").getBytes(), StandardOpenOption.APPEND);

		path = Paths.get(dir + "root.txt");
		Files.write(path, (keyCap+"\n").getBytes(), StandardOpenOption.APPEND);
		return new Capability(keyCap);
	}
	public String getKey(String resource, String operation) throws IOException {
		File file = new File(dir + "capabilities.txt");
		Scanner input = new Scanner(file);
		String capKey = null;
		while (input.hasNextLine()) {
			String cap = input.nextLine();
			if (cap.contains(resource) && cap.contains(operation)) {
				String[] newCap = cap.split(":");
				capKey = newCap[1];
			}
		}
		input.close();
		return capKey;
	}

	public void givePermission(String resource, String operation, String user) throws IOException {
		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dir + user + ".txt"), "utf-8"))) {
			writer.write(getKey(resource, operation));
		}
	}

	public boolean checkPermission(List<Capability> x, String resource, String operation) throws IOException {
		String key = getKey(resource, operation);
		for (Capability cap : x) {
			if (cap.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}

	public String encrypt(String Data) throws Exception {
		Cipher c = Cipher.getInstance(ALGO);
		Key key = new SecretKeySpec(keyValue,ALGO);
		c.init(Cipher.ENCRYPT_MODE,key);
		byte[] encVal = c.doFinal(Data.getBytes());
		return getEncoder().encodeToString(encVal);
	}
}
