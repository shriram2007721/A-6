package com.emergency.model;

public enum AmbulanceState { 
    AVAILABLE, 
    DISPATCHED, 
    EN_ROUTE, 
    PATIENT_PICKED_UP, 
    HOSPITAL_ARRIVED;
    
    public AmbulanceState next() {
        return switch (this) {
            case AVAILABLE -> DISPATCHED;
            case DISPATCHED -> EN_ROUTE;
            case EN_ROUTE -> PATIENT_PICKED_UP;
            case PATIENT_PICKED_UP -> HOSPITAL_ARRIVED;
            case HOSPITAL_ARRIVED -> AVAILABLE;
        };
    }
}
