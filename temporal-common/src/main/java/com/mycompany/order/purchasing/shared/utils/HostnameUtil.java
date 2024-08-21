package com.mycompany.order.purchasing.shared.utils;


public class HostnameUtil {

    private HostnameUtil() {}
    
    /**
     * Get the host name of the computer
     * 
     * @return Value stored in the HOSTNAME or COMPUTERNAME env variables
     */
    public static String getHostname() {
        String hostname = System.getenv("COMPUTERNAME"); // On Windows
        if (hostname == null || hostname.isEmpty()) {
            hostname = System.getenv("HOSTNAME"); // On Unix/Linux
        }

        return hostname;
    }
}
