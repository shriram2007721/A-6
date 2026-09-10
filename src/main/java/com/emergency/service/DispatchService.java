package com.emergency.service;

import com.emergency.exception.*;
import com.emergency.model.*;
import java.util.*;

public class DispatchService {
    private final Map<String, Ambulance> fleet = new HashMap<>();
    private final PriorityQueue<EmergencyRequest> waitingQueue = new PriorityQueue<>();
    private final List<EmergencyRequest> history = new ArrayList<>();

    public synchronized void registerAmbulance(Ambulance ambulance) {
        if (ambulance == null || ambulance.getId() == null) {
            throw new InvalidRequestException("Invalid ambulance data provided.");
        }
        fleet.put(ambulance.getId(), ambulance);
    }

    public synchronized void processEmergencyRequest(EmergencyRequest request) {
        validateRequest(request);
        history.add(request);

        boolean assigned = allocateAmbulance(request);
        if (!assigned) {
            waitingQueue.add(request);
        }
    }

    private boolean allocateAmbulance(EmergencyRequest request) {
        Ambulance bestMatch = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Ambulance ambulance : fleet.values()) {
            if (ambulance.getState() == AmbulanceState.AVAILABLE && isTypeAppropriate(request.getPriority(), ambulance.getType())) {
                if (request.getEstimatedDistance() < shortestDistance) {
                    shortestDistance = request.getEstimatedDistance();
                    bestMatch = ambulance;
                }
            }
        }

        if (bestMatch != null) {
            bestMatch.setState(AmbulanceState.DISPATCHED);
            bestMatch.setCurrentRequestId(request.getPatientId());
            
            request.setAssignedAmbulanceId(bestMatch.getId());
            request.setStatus(EmergencyStatus.DISPATCHED);
            request.setEstimatedArrivalTime(request.getEstimatedDistance() / 0.75); // 45 km/h standard
            return true;
        }
        return false;
    }

    private boolean isTypeAppropriate(EmergencyPriority priority, AmbulanceType type) {
        return switch (priority) {
            case CRITICAL -> type == AmbulanceType.ICU;
            case HIGH -> type == AmbulanceType.ADVANCED_LIFE_SUPPORT || type == AmbulanceType.ICU;
            case MODERATE, NORMAL -> true;
        };
    }

    public synchronized void updateAmbulanceState(String ambulanceId, AmbulanceState newState) {
        Ambulance ambulance = fleet.get(ambulanceId);
        if (ambulance == null) {
            throw new ResourceUnavailableException("Ambulance ID context does not exist.");
        }

        ambulance.setState(newState);

        Optional<EmergencyRequest> activeReq = history.stream()
            .filter(r -> ambulanceId.equals(r.getAssignedAmbulanceId()) && r.getStatus() != EmergencyStatus.HOSPITAL_ARRIVED)
            .findFirst();

        if (activeReq.isPresent()) {
            EmergencyRequest req = activeReq.get();
            if (newState == AmbulanceState.EN_ROUTE) req.setStatus(EmergencyStatus.EN_ROUTE);
            else if (newState == AmbulanceState.PATIENT_PICKED_UP) req.setStatus(EmergencyStatus.PATIENT_PICKED_UP);
            else if (newState == AmbulanceState.HOSPITAL_ARRIVED) {
                req.setStatus(EmergencyStatus.HOSPITAL_ARRIVED);
                ambulance.setState(AmbulanceState.AVAILABLE);
                ambulance.setCurrentRequestId(null);
                processWaitingQueue();
            }
        } else if (newState == AmbulanceState.AVAILABLE) {
            processWaitingQueue();
        }
    }

    private void processWaitingQueue() {
        if (!waitingQueue.isEmpty()) {
            EmergencyRequest nextInLine = waitingQueue.peek();
            if (allocateAmbulance(nextInLine)) {
                waitingQueue.poll();
            }
        }
    }

    private void validateRequest(EmergencyRequest request) {
        if (request == null || request.getPatientId() == null || request.getPickupLocation() == null) {
            throw new InvalidRequestException("Emergency structural profile contains invalid fields.");
        }
    }

    public List<EmergencyRequest> getHistory() { return new ArrayList<>(history); }
    public PriorityQueue<EmergencyRequest> getWaitingQueue() { return waitingQueue; }
    public Map<String, Ambulance> getFleet() { return fleet; }
}
