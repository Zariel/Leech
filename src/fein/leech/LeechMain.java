package fein.leech;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fein.leech.io.Writer;

public class LeechMain {	
	private Writer IO = new Writer();
	private ExecutorService threadpool;
	private ExecutorCompletionService<String> pool;

	public LeechMain(String host, int port, int num_connections) {
		threadpool = Executors.newFixedThreadPool(num_connections);
		pool = new ExecutorCompletionService<String>(threadpool);

		pool.submit(new Downloader(host, port, IO));
		
		try {
			System.out.println(pool.take().get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		threadpool.shutdown();
	}
}
