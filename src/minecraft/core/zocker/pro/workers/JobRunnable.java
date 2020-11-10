package minecraft.core.zocker.pro.workers;

public abstract class JobRunnable implements Runnable {

    private Job job;
    private String method, className;

    /**
     * Instantiates a new Job runnable.
     *
     * @param method    the method
     * @param className the class name
     */
    public JobRunnable(String method, String className) {
        this.method = method;
        this.className = className;
    }

    /**
     * Is cancelled boolean.
     *
     * @return the boolean
     * @throws IllegalStateException the illegal state exception
     */
    public synchronized boolean isCancelled() throws IllegalStateException {
        return this.job.isCancelled();
    }

    /**
     * Cancel.
     *
     * @throws IllegalStateException the illegal state exception
     */
    public synchronized void cancel() throws IllegalStateException {
        job.cancel(false);
    }

    /**
     * Cancel.
     *
     * @param forceStop the force stop
     * @throws IllegalStateException the illegal state exception
     */
    public synchronized void cancel(Boolean forceStop) throws IllegalStateException {
        job.cancel(forceStop);
    }

    /**
     * Gets job.
     *
     * @return the job
     * @throws IllegalStateException the illegal state exception
     */
    public synchronized Job getJob() throws IllegalStateException {
        return this.job;
    }

    /**
     * Initialize.
     *
     * @param job the job
     */
    protected void initialize(Job job) {
        this.job = job;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return method + "@" + className;
    }

    /**
     * Sets method.
     *
     * @param method the method
     * @return the method
     */
    public JobRunnable setMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * Sets class name.
     *
     * @param className the class name
     * @return the class name
     */
    public JobRunnable setClassName(String className) {
        this.className = className;
        return this;
    }

}
