package com.emergency.service;

import com.emergency.exception.*;
import com.emergency.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DispatchServiceTest {
    private DispatchService service;

    @BeforeEach
    public void setup() {
        service = new DispatchService();
    }

    @Test
    public void testCriticalPriorityAllocationAndQueueCascading() {
        Ambulance icuAmbulance = new Ambulance("AMB-ICU-01", AmbulanceType.ICU, "Dr. Banner", "999");
        service.registerAmbulance(icuAmbulance);

        EmergencyRequest critRequest1 = new EmergencyRequest("P-101", "Cardiac", "Zone A", "City Hosp", EmergencyPriority.CRITICAL, 6.0);
        service.processEmergencyRequest(critRequest1);

        assertEquals("AMB-ICU-01", critRequest1.getAssignedAmbulanceId());
        assertEquals(AmbulanceState.DISPATCHED, icuAmbulance.getState());
        assertEquals(8.0, critRequest1.getEstimatedArrivalTime());

        EmergencyRequest critRequest2 = new EmergencyRequest("P-102", "Stroke", "Zone B", "General Hosp", EmergencyPriority.CRITICAL, 3.0);
        service.processEmergencyRequest(critRequest2);

        assertEquals(1, service.getWaitingQueue().size());
        assertEquals(EmergencyStatus.WAITING, critRequest2.getStatus());

        service.updateAmbulanceState("AMB-ICU-01", AmbulanceState.EN_ROUTE);
        service.updateAmbulanceState("AMB-ICU-01", AmbulanceState.PATIENT_PICKED_UP);
        service.updateAmbulanceState("AMB-ICU-01", AmbulanceState.HOSPITAL_ARRIVED);

        assertTrue(service.getWaitingQueue().isEmpty());
        assertEquals("AMB-ICU-01", critRequest2.getAssignedAmbulanceId());
        assertEquals(EmergencyStatus.DISPATCHED, critRequest2.getStatus());
    }

    @Test
    public void testInvalidValidationThrowsException() {
        assertThrows(InvalidRequestException.class, () -> {
            service.processEmergencyRequest(new EmergencyRequest(null, "Injury", null, "Hosp", EmergencyPriority.NORMAL, 1.0));
        });
    }
}
