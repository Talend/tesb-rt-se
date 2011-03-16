package routines;

/**
 * Interface describing Job behaviors.
 */
public interface TalendJob {

    /**
     * Run a Talend job.
     *
     * @param args job arguments.
     * @return an array of value per row returned.
     */
    public String[][] runJob(String[] args);

    /**
     * Run a Talend job.
     *
     * @param args job arguments.
     * @return return code, if 0 execution completed successfully, else execution failed.
     */
    public int runJobInTOS(String[] args);

}
