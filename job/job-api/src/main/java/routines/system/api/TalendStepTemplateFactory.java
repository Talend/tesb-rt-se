package routines.system.api;

/**
 * A factory interface to create to create specific kind of {@link TalendStepTemplate}. The factor allows the
 * Talend Runtime to create several instances of the job and to enable concurrent access. 
 */
public interface TalendStepTemplateFactory {

    /**
     * Creates a new {@link TalendJob}. All instances returned must be different and of the same type.
     * 
     * @return a new {@link TalendJob} instance,  must not be <code>null</code>.
     */
    TalendJob newTalendStepTemplate(IPaasObject object);

}
