package wbt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class Passphrase {
	
	private static final String filePath = "/mnt/3E9ED9A49ED954CD/GitHub/securicast/service_provider/WBT-SP/passphrasesList.txt";
	private static String[] passhrasesList = null;
	
	
	/**
	 * http://www.manythings.org/vocabulary/lists/l/words.php?f=ogden-picturable
	 * bzw.
	 * Words from a simplified language by Charles K. Ogden (1930)
	 */
	public static void fillPassphraseList() {
		Path path = Paths.get(filePath);
		try {
			// https://stackoverflow.com/a/23079174
			passhrasesList = Files.lines(path).toArray(String[]::new); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getPassphrase() {
		String[] passphrase = new String[4];
		SecureRandom random = new SecureRandom();
		for (int i = 0; i<4; ++i) {
			passphrase[i] = passhrasesList[random.nextInt(passhrasesList.length)];
		}
		
		return String.join(", ", passphrase);
	}

}
