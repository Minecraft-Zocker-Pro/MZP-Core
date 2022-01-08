package minecraft.core.zocker.pro.workers.instances;

import minecraft.core.zocker.pro.workers.Job;
import minecraft.core.zocker.pro.workers.Worker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public enum Workers {

	/**
	 * The Default worker.
	 */
	DEFAULT_WORKER(Worker.getDefaultWorker()),

	/**
	 * The Player worker.
	 */
	PLAYER_WORKER("Player Handling Worker"),

	/**
	 * The Packet worker.
	 */
	PACKET_WORKER("Packet Handling Worker"),

	/**
	 * The Handshake worker.
	 */
	HANDSHAKE_WORKER("Handshake Handle Worker"),

	/**
	 * The Backend worker.
	 */
	BACKEND_WORKER("Backend Worker"),

	/**
	 * The Frontend worker.
	 */
	FRONTEND_WORKER("Frontend Worker");

	/**
	 * The Worker priority worker hash map.
	 */
	private final HashMap<WorkerPriority, Worker> workerPriorityWorkerHashMap = new HashMap<>();

	/**
	 * Instantiates a new Workers.
	 *
	 * @param worker the worker
	 */
	Workers(Worker worker) {
		for (WorkerPriority value : WorkerPriority.values()) workerPriorityWorkerHashMap.put(value, worker);
	}

	/**
	 * Instantiates a new Workers.
	 *
	 * @param name the name
	 */
	Workers(String name) {
		for (WorkerPriority value : WorkerPriority.values())
			workerPriorityWorkerHashMap.put(value, Worker.createNewWorker(name + String.format(" [PRIO-%s]", value.name())));
	}

	/**
	 * Gets worker.
	 *
	 * @param priority the priority
	 * @return the worker
	 */
	public Worker getWorker(WorkerPriority priority) {
		return workerPriorityWorkerHashMap.get(priority);
	}

	/**
	 * Add job job.
	 *
	 * @param runnable the runnable
	 * @param priority the priority
	 * @return the job
	 */
	public Job addJob(Runnable runnable, WorkerPriority priority) {
		return this.getWorker(priority).addJob(runnable);
	}

	/**
	 * Add job job.
	 *
	 * @param runnable the runnable
	 * @param delay    the delay
	 * @param timeUnit the time unit
	 * @param priority the priority
	 * @return the job
	 */
	public Job addJob(Runnable runnable, int delay, TimeUnit timeUnit, WorkerPriority priority) {
		return this.getWorker(priority).addJob(runnable, delay, timeUnit);
	}

	/**
	 * Add job job.
	 *
	 * @param runnable the runnable
	 * @param delay    the delay
	 * @param period   the period
	 * @param timeUnit the time unit
	 * @param priority the priority
	 * @return the job
	 */
	public Job addJob(Runnable runnable, int delay, int period, TimeUnit timeUnit, WorkerPriority priority) {
		return this.getWorker(priority).addJob(runnable, delay, period, timeUnit);
	}


}
