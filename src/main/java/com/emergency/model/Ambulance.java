package com.emergency.model;

public class Ambulance {
    private final String id;
    private final AmbulanceType type;
    private final String driverName;
    private final String driverContact;
    private AmbulanceState state;
    private String currentRequestId;

    public Ambulance(String id, AmbulanceType type, String driverName, String driverContact) {
        this.id = id;
        this.type = type;
        this.driverName = driverName;
        this.driverContact = driverContact;
        this.state = AmbulanceState.AVAILABLE;
    }

    public String getId() { return id; }
    public AmbulanceType getType() { return type; }
    public String getDriverName() { return driverName; }
    public String getDriverContact() { return driverContact; }
    public AmbulanceState getState() { return state; }
    public void setState(AmbulanceState state) { this.state = state; }
    public String getCurrentRequestId() { return currentRequestId; }
    public void setCurrentRequestId(String currentRequestId) { this.currentRequestId = currentRequestId; }
}
