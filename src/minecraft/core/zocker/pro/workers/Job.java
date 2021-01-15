package minecraft.core.zocker.pro.workers;

import minecraft.core.zocker.pro.util.Validator;

import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Job {

	private final Runnable runnable;
	private final long timeDelay;
	private final long timeInterval;
	private final TimeUnit timeUnit;
	private final ScheduledThreadPoolExecutor executor;
	private ScheduledFuture<?> scheduledFuture;
	private Future<?> future;

	/**
	 * Instantiates a new Job.
	 *
	 * @param runnable     the runnable
	 * @param timeDelay    the time delay
	 * @param timeInterval the time interval
	 * @param timeUnit     the time unit
	 * @param executor     the executor
	 */
	protected Job(Runnable runnable, long timeDelay, long timeInterval, TimeUnit timeUnit, ScheduledThreadPoolExecutor executor) {
		Validator.checkNotNull(executor, "The executor cannot be null.");

		if (runnable instanceof JobRunnable) {
			JobRunnable jobRunnable = (JobRunnable) runnable;
			jobRunnable.initialize(this);
		}

		this.runnable = runnable;
		this.timeDelay = timeDelay;
		this.timeInterval = timeInterval;
		this.timeUnit = timeUnit;
		this.executor = executor;

		boolean isScheduled = timeUnit != null;
		boolean isRepeated = timeInterval > 0;

		if (!isScheduled && !isRepeated)
			this.future = this.executor.submit(runnable);

		if (isScheduled && !isRepeated)
			this.scheduledFuture = this.executor.schedule(runnable, timeDelay, timeUnit);

		if (isRepeated)
			this.scheduledFuture = this.executor.scheduleAtFixedRate(runnable, timeDelay, timeInterval, Objects.requireNonNull(timeUnit));
	}

	/**
	 * Cancel boolean.
	 *
	 * @return the boolean
	 */
	public boolean cancel() {
		return this.cancel(false);
	}

	/**
	 * Cancel boolean.
	 *
	 * @param forceStop the force stop
	 * @return the boolean
	 */
	public boolean cancel(Boolean forceStop) {
		Future<?> future = this.scheduledFuture;
		if (future == null) future = this.future;

		return future.cancel(forceStop);
	}

	/**
	 * Restart boolean.
	 *
	 * @return the boolean
	 */
	public boolean restart() {
		Future<?> future = this.scheduledFuture;
		if (future == null) future = this.future;

		if (!future.isCancelled()) {
			Validator.checkNotNull(null, "You can't restart a running job!");
			return false;
		}

		boolean isScheduled = timeUnit != null;
		boolean isRepeated = timeInterval > 0;

		if (!isScheduled && !isRepeated)
			this.future = this.executor.submit(runnable);

		if (isScheduled && !isRepeated)
			this.scheduledFuture = this.executor.schedule(runnable, timeDelay, timeUnit);

		if (isRepeated)
			this.scheduledFuture = this.executor.scheduleAtFixedRate(runnable, timeDelay, timeInterval, timeUnit);

		future = this.scheduledFuture;
		if (future == null) future = this.future;

		return !future.isCancelled();
	}

	/**
	 * Gets job.
	 *
	 * @return the job
	 */
	public Job getJob() {
		return this;
	}

	/**
	 * Is cancelled boolean.
	 *
	 * @return the boolean
	 */
	public boolean isCancelled() {
		boolean isScheduled = timeUnit != null;
		boolean isRepeated = timeInterval > 0;

		if (!isScheduled && !isRepeated) {
			return this.future.isCancelled();
		}

		if (isScheduled && !isRepeated) {
			return scheduledFuture.isCancelled();
		}

		return scheduledFuture.isCancelled();
	}

	/**
	 * Is done boolean.
	 *
	 * @return the boolean
	 */
	public boolean isDone() {
		return future.isDone();
	}
}
