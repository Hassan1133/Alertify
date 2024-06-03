package com.example.alertify.main_utils;

public class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371; // Radius of the earth in kilometers

    public static double calculateDistance(double userLat, double userLong, double policeStationLat, double policeStationLong) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(userLat);
        double lon1Rad = Math.toRadians(userLong);
        double lat2Rad = Math.toRadians(policeStationLong);
        double lon2Rad = Math.toRadians(policeStationLong);

        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // Distance in kilometers
    }
}
