package fein.leech;

import java.io.IOException;
import java.util.concurrent.Callable;

import fein.leech.io.Writer;
import fein.leech.usenet.UsenetStream;

public class Downloader implements Callable<String> {
	private Writer IO;
	private UsenetStream usenet;
	private String host;
	private int port;
	
	public Downloader(String host, int port, Writer writer) {
		IO = writer;
		this.host = host;
		this.port = port;
	}
	
	protected void finalize() throws Throwable {
		try {
			usenet.close();
		} finally {
			super.finalize();
		}
	}

	@Override
	public String call() throws Exception {
		try {
			usenet = new UsenetStream(host, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
		
		usenet.send("HELP");
		String read = new String(usenet.read(), "US-ASCII");
		
		return read;
	}
}
