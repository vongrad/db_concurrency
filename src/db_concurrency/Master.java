package db_concurrency;

import db_concurrency.connector.IConnector;
import db_concurrency.connector.OraclePoolConnector;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Master implements Runnable, IStatistics {
	private Map<Integer, StatisticResult> statMap = new ConcurrentHashMap<Integer, StatisticResult>();
	private Map<StatisticResult, Integer> statCount = new HashMap<StatisticResult, Integer>();

	public static void main(String[] args) {
		Logger.getGlobal().setLevel(Level.ALL);

		//start the monitoring thread 
		int numOfSimultantClients = 10;
		IConnector connector = new OraclePoolConnector();
		Thread masterThread = new Thread(new Master(numOfSimultantClients, connector));
		masterThread.start();
	}

	private IConnector conn;
	private ThreadPoolExecutor executor;
	private boolean run = true;
	private int threadClientId;

	public Master(int numOfSimultantClients, IConnector connector) {
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numOfSimultantClients);
		this.conn = connector;
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
		Reservation res = null;
		try {
			res = new Reservation(conn.getConnection());

			//Initial fill up.
			System.out.println("Max: " + executor.getMaximumPoolSize());
			System.out.println("Pool: " + executor.getPoolSize());
			System.out.println("CorePool: " + executor.getCorePoolSize());
			for(int i = 0 ; i < executor.getMaximumPoolSize() ; i++) {
				Logger.getLogger(Master.class.getName()).log(Level.INFO, "Spawn: " + i);
				spawnClient(executor);
			}
			while(run && !res.isAllBooked(getPlaneNr())) {
				String output = String.format(
						"[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
						this.executor.getActiveCount(),
						this.executor.getCorePoolSize(),
						this.executor.getActiveCount(),
						this.executor.getCompletedTaskCount(),
						this.executor.getTaskCount(),
						this.executor.isShutdown(),
						this.executor.isTerminated());
				System.out.println(output);

				if(executor.getActiveCount() < executor.getCorePoolSize()) {
					// Needs to spawn.
					System.out.println("Spawn");
					spawnClient(executor);
				} else {
					try {
						Thread.sleep(1);
					} catch(InterruptedException ex) {
						Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			// Tell threads to finish off.
			executor.shutdown();
			try {
				// Wait for everything to finish.
				while(!executor.awaitTermination(2, TimeUnit.SECONDS)) {
					Logger.getLogger(Master.class.getName()).log(Level.INFO, "Awaiting completion of threads.");
				}
			} catch(InterruptedException ex) {
				Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
			}
			Logger.getLogger(Master.class.getName()).log(Level.INFO, "Master is stopping");
		} catch(SQLException ex) {
			Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				if(res == null) {
					res.close();
				}
			} catch(Exception ignored) {
			}
		}
	}

<<<<<<< HEAD
	private void spawnClient(ThreadPoolExecutor executor) {
		executor.execute(new Client(conn, nextThreadClientId(), getPlaneNr()));
=======
	private void spawnClient(ThreadPoolExecutor executor) throws SQLException {
		executor.execute(new Client(conn.getConnection(), nextThreadClientId(), getPlaneNr(), this));
	}

	@Override
	public synchronized void putStat(int clientid, StatisticResult statisticResult) {
		this.statMap.put(clientid, statisticResult);
		if(this.statCount.containsKey(statisticResult)) {
			Integer count = statCount.get(statisticResult);
			statCount.put(statisticResult, count + 1);
		} else {
			statCount.put(statisticResult, Integer.valueOf(1));
		}
	}

	public void printTheStats() {
		System.out.println("Statistics:");
		System.out.println("-----------");
		System.out.println("");
		for(StatisticResult result : statCount.keySet()) {
			System.out.println(result.toString() + " = " + statCount.get(result));
		}
>>>>>>> 0e42f38c51a2424c1ec65744739582f4acbd3358
	}
}
