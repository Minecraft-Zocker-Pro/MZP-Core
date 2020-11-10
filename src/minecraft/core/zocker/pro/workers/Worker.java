package minecraft.core.zocker.pro.workers;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.util.Validator;

import java.util.UUID;
import java.util.concurrent.*;

public class Worker {

	/**
	 * The constant default_worker.
	 */
	private static final Worker default_worker = Worker.createNewWorker("default worker");

	/**
	 * The Maximum pool size.
	 */
	private final Integer maximumPoolSize = 7;
	/**
	 * The Pool executor.
	 */
	private final ScheduledThreadPoolExecutor poolExecutor =
		(ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(maximumPoolSize);
	/**
	 * The Worker id.
	 */
	private final UUID workerID;
	/**
	 * The Queue.
	 */
	private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	/**
	 * The Worker name.
	 */
	private String workerName;

	/**
	 * Instantiates a new Worker.
	 *
	 * @param workerName the worker name
	 */
	private Worker(String workerName) {
		Validator.checkNotNull(workerName, "The worker name cannot be null!");
		Validator.checkNotNull(maximumPoolSize, "The Maximum Pools size cannot be null.");

		this.workerID = UUID.randomUUID();
		this.setWorkerName(workerName);
	}

	/**
	 * Create new worker worker.
	 *
	 * @param name the name
	 * @return the worker
	 */
	public static Worker createNewWorker(String name) {
		return new Worker(name);
	}

	/**
	 * Gets default worker.
	 *
	 * @return the default worker
	 */
	public static Worker getDefaultWorker() {
		return default_worker;
	}

	/**
	 * Add job job.
	 *
	 * @param runnable the runnable
	 * @return the job
	 */
	public Job addJob(Runnable runnable) {
		return this.pushJob(runnable, -1, -1, null);
	}

	/**
	 * Add job job.
	 *
	 * @param runnable the runnable
	 * @param delay    the delay
	 * @param timeUnit the time unit
	 * @return the job
	 */
	public Job addJob(Runnable runnable, int delay, TimeUnit timeUnit) {
		return this.pushJob(runnable, delay, -1, timeUnit);
	}

	/**
	 * Add job job.
	 *
	 * @param runnable the runnable
	 * @param delay    the delay
	 * @param period   the period
	 * @param timeUnit the time unit
	 * @return the job
	 */
	public Job addJob(Runnable runnable, int delay, int period, TimeUnit timeUnit) {
		return this.pushJob(runnable, delay, period, timeUnit);
	}

	/**
	 * Push job job.
	 *
	 * @param runnable the runnable
	 * @param delay    the delay
	 * @param period   the period
	 * @param timeUnit the time unit
	 * @return the job
	 */
	private Job pushJob(Runnable runnable, int delay, int period, TimeUnit timeUnit) {
		return new Job(runnable, delay, period, timeUnit, poolExecutor);
	}

	/**
	 * Gets worker name.
	 *
	 * @return the worker name
	 */
	public String getWorkerName() {
		return workerName;
	}

	/**
	 * Sets worker name.
	 *
	 * @param workerName the worker name
	 */
	public void setWorkerName(String workerName) {
		this.workerName = String.format("[%s-%s-%s] W%s (%s)",
			Main.getPlugin().getPluginName(),
			"v0.1",
			"STABLE",
			this.workerID.toString().substring(0, 8),
			workerName.replace(" ", ""
				.toUpperCase()));
	}

	/**
	 * Gets worker id.
	 *
	 * @return the worker id
	 */
	public UUID getWorkerID() {
		return workerID;
	}

	/**
	 * Gets worker.
	 *
	 * @return the worker
	 */
	public Worker getWorker() {
		return this;
	}

}
