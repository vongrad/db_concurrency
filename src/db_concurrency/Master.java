package db_concurrency;

import db_concurrency.connector.IConnector;
import db_concurrency.connector.OraclePoolConnector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Preuss
 */
public class Master implements Runnable {

    public static void main(String[] args) {
		Logger.getGlobal().setLevel(Level.ALL);
		
        //start the monitoring thread 
		int numOfSimultantClients = 10;
		IConnector connector = new OraclePoolConnector();
        Thread masterThread = new Thread(new Master(numOfSimultantClients, connector));
        masterThread.start();
    }

	private IConnector conn;
	private Reservation res;
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
        res = new Reservation(conn);
        //Initial fill up.
        for (int i = 0; i < executor.getCorePoolSize(); i++) {
			Logger.getLogger(OraclePoolConnector.class.getName()).log(Level.INFO, "Spawn: " + i);
            spawnClient(executor);
        }
		if(true)return;

        while (run && res.isAllBooked(getPlaneNr())) {
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

            if (executor.getPoolSize() < executor.getCorePoolSize()) {
                // Needs to spawn.
                spawnClient(executor);
            }
        }
    }

    private void spawnClient(ThreadPoolExecutor executor) {
        executor.execute(new Client(res, nextThreadClientId(), getPlaneNr()));
    }
}
