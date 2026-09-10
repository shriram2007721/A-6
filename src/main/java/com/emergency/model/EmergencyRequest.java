package com.emergency.model;

import java.time.Instant;

public class EmergencyRequest implements Comparable<EmergencyRequest> {
    private final String patientId;
    private final String emergencyType;
    private final String pickupLocation;
    private final String destinationHospital;
    private final EmergencyPriority priority;
    private final double estimatedDistance; // in km
    private final Instant timestamp;
    
    private EmergencyStatus status;
    private String assignedAmbulanceId;
    private double estimatedArrivalTime; // in minutes

    public EmergencyRequest(String patientId, String emergencyType, String pickupLocation, 
                            String destinationHospital, EmergencyPriority priority, double estimatedDistance) {
        this.patientId = patientId;
        this.emergencyType = emergencyType;
        this.pickupLocation = pickupLocation;
        this.destinationHospital = destinationHospital;
        this.priority = priority;
        this.estimatedDistance = estimatedDistance;
        this.status = EmergencyStatus.WAITING;
        this.timestamp = Instant.now();
    }

    @Override
    public int compareTo(EmergencyRequest other) {
        int priorityCompare = this.priority.ordinal() - other.priority.ordinal();
        if (priorityCompare != 0) return priorityCompare;
        return this.timestamp.compareTo(other.timestamp);
    }

    public String getPatientId() { return patientId; }
    public String getEmergencyType() { return emergencyType; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestinationHospital() { return destinationHospital; }
    public EmergencyPriority getPriority() { return priority; }
    public double getEstimatedDistance() { return estimatedDistance; }
    public EmergencyStatus getStatus() { return status; }
    public void setStatus(EmergencyStatus status) { this.status = status; }
    public String getAssignedAmbulanceId() { return assignedAmbulanceId; }
    public void setAssignedAmbulanceId(String id) { this.assignedAmbulanceId = id; }
    public double getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(double eta) { this.estimatedArrivalTime = eta; }
}
