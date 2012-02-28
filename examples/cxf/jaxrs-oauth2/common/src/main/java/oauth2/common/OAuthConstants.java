/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.common;



public final class OAuthConstants {

    // Default scope/permission associated with tokens: 
    // a client, if authorized, can read a calendar of a given user 
    public static String READ_CALENDAR_SCOPE = "readCalendar";
    public static String READ_CALENDAR_DESCRIPTION = "Read the calendar";
    
    // The scope/permission allowing to update a calendar but only
    // at a given hour slot. For example, if a user requested the 3rd
    // party service to book a table at 7 then the client will only 
    // be able to update the calendar at the 7 hour slot - to be enforced 
    // by a custom filter.
    
    // Format: updateCalendar-x, where x is an hour value, for example:
    // updateCalendar-7
    
    // Note that a simpler format such as "updateCalendar" would let the client to
    // update the calendar at any time slot.
    
    public static String UPDATE_CALENDAR_SCOPE = "updateCalendar-";
    public static String UPDATE_CALENDAR_DESCRIPTION = "Update the calendar at ";
    
    private OAuthConstants() {
        
    }
    
}
