package db_concurrency;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Preuss
 */
public class Master implements Runnable {
	public static void main(String[] args) {
		//Get the ThreadFactory implementation to use 
		Executor threadFactory = Executors.newFixedThreadPool(10);

		//creating the ThreadPoolExecutor 
		ThreadPoolExecutor executorPool = (ThreadPoolExecutor) threadFactory;

		//start the monitoring thread 
		Thread masterThread = new Thread(new Master(executorPool, 10));
		masterThread.start();
	}

	private ThreadPoolExecutor executor;
	private int seconds;
	private boolean run = true;
	private int threadClientId;

	public Master(ThreadPoolExecutor executor, int delay) {
		this.executor = executor;
		this.seconds = delay;
	}

	public void shutdown() {
		this.run = false;
	}

	private int nextThreadClientId() {
		return ++threadClientId;
	}

	private String getPlaneNr() {
		return "CR9";
	}

	@Override
	public void run() {
		Reservation reservation = new Reservation();
		//Initial fill up.
		for(int i = 0 ; i < executor.getCorePoolSize() ; i++) {
			spawnClient(executor);
		}

		while(run) {
			if(reservation.isAllBooked(getPlaneNr())) {
				shutdown();
				return;
			}

			String output = String.format(
					"[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
					this.executor.getPoolSize(),
					this.executor.getCorePoolSize(),
					this.executor.getActiveCount(),
					this.executor.getCompletedTaskCount(),
					this.executor.getTaskCount(),
					this.executor.isShutdown(),
					this.executor.isTerminated());
			System.out.println(output);

			if(executor.getPoolSize() < executor.getCorePoolSize()) {
				// Needs to spawn.
				spawnClient(executor);
			}
		}
	}

	private void spawnClient(ThreadPoolExecutor executor) {
		executor.execute(new Client(nextThreadClientId(), getPlaneNr()));
	}
}
