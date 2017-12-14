package jp.noorg.scraping;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

import org.junit.Test;

import com.msopentech.thali.java.toronionproxy.JavaOnionProxyContext;
import com.msopentech.thali.java.toronionproxy.JavaOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import com.msopentech.thali.toronionproxy.Utilities;

public class ScrapingTest {

	@Test
	public void test() throws IOException, InterruptedException {
		String fileStorageLocation = "torfiles";
		OnionProxyManager onionProxyManager = new JavaOnionProxyManager(
				new JavaOnionProxyContext(
						Files.createTempDirectory(fileStorageLocation).toFile()));

		int totalSecondsPerTorStartup = 4 * 60;
		int totalTriesPerTorStartup = 5;

		// Start the Tor Onion Proxy
		if (onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup) == false) {
			return;
		}
		//Document document = Jsoup.connect("http://www.google.co.jp").proxy("127.0.0.1", 9343).get();
		//System.out.println("LLLL");
		//System.out.println(document.html());

		// Start a hidden service listener
		int hiddenServicePort = 80;
		int localPort = 9343;
		String onionAddress = onionProxyManager.publishHiddenService(hiddenServicePort, localPort);

		// It can taken anywhere from 30 seconds to a few minutes for Tor to start properly routing
		// requests to to a hidden service. So you generally want to try to test connect to it a
		// few times. But after the previous call the Tor Onion Proxy will route any requests
		// to the returned onionAddress and hiddenServicePort to 127.0.0.1:localPort. So, for example,
		// you could just pass localPort into the NanoHTTPD constructor and have a HTTP server listening
		// to that port.

		// Connect via the TOR network
		// In this case we are trying to connect to the hidden service but any IP/DNS address and port can be
		// used here.
		Socket clientSocket = Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, "127.0.0.1",
				localPort);
	}

}
