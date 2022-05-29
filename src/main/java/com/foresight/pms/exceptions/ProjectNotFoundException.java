package com.foresight.pms.exceptions;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String m) {
        super("Project by UID " + m + " not found!");
    }
}
