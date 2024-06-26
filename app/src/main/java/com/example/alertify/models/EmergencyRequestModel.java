package com.example.alertify.models;

import java.io.Serializable;

public class EmergencyRequestModel implements Serializable {

    private String requestId;
    private String requestStatus;
    private String userId;
    private String policeStation;
    private String requestDateTime;
    private double userCurrentLatitude;
    private double userCurrentLongitude;
    private boolean forwarded;

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public double getUserCurrentLatitude() {
        return userCurrentLatitude;
    }

    public void setUserCurrentLatitude(double userCurrentLatitude) {
        this.userCurrentLatitude = userCurrentLatitude;
    }

    public double getUserCurrentLongitude() {
        return userCurrentLongitude;
    }

    public void setUserCurrentLongitude(double userCurrentLongitude) {
        this.userCurrentLongitude = userCurrentLongitude;
    }

    public String getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }
}
