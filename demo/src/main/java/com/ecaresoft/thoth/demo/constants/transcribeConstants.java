package com.ecaresoft.thoth.demo.constants;

public class transcribeConstants {

    private transcribeConstants(){
        throw new IllegalStateException("Utility class");
    }
    public static final String SPECIALTY = "PRIMARYCARE";
    public static final String TYPE = "DICTATION";
    public static final String LAN_CODE = "en-US";
    public static final String JOBS_OUTPUT_FOLDER = "audiojobs/";
}