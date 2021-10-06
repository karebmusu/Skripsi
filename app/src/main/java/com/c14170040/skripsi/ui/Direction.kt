package com.c14170040.skripsi.ui

import com.google.gson.annotations.SerializedName

data class Direction(

        @SerializedName( "routes")
        val routes: List<RoutesItem?>,

        @SerializedName( "geocoded_waypoints")
        val geocodedWaypoints: List<GeocodedWaypointsItem?> ,

        @SerializedName( "status")
        val status: String
)

data class StepsItem(

        @SerializedName( "duration")
        val duration: Duration ,

        @SerializedName( "start_location")
        val startLocation: StartLocation ,

        @SerializedName( "distance")
        val distance: Distance ,

        @SerializedName( "travel_mode")
        val travelMode: String ,

        @SerializedName( "html_instructions")
        val htmlInstructions: String ,

        @SerializedName( "end_location")
        val endLocation: EndLocation ,

        @SerializedName( "maneuver")
        val maneuver: String ,

        @SerializedName( "polyline")
        val polyline: Polyline
)

data class OverviewPolyline(

        @SerializedName( "points")
        val points: String
)

data class Distance(

        @SerializedName( "text")
        val text: String ,

        @SerializedName( "value")
        val value: Int
)

data class Southwest(

        @SerializedName( "lng")
        val lng: Double ,

        @SerializedName( "lat")
        val lat: Double
)

data class Polyline(

        @SerializedName( "points")
        val points: String
)

data class GeocodedWaypointsItem(

        @SerializedName( "types")
        val types: List<String?> ,

        @SerializedName( "geocoder_status")
        val geocoderStatus: String ,

        @SerializedName( "place_id")
        val placeId: String
)

data class Bounds(

        @SerializedName( "southwest")
        val southwest: Southwest ,

        @SerializedName( "northeast")
        val northeast: Northeast
)

data class Northeast(

        @SerializedName( "lng")
        val lng: Double ,

        @SerializedName( "lat")
        val lat: Double
)

data class RoutesItem(

        @SerializedName( "summary")
        val summary: String ,

        @SerializedName( "copyrights")
        val copyrights: String ,

        @SerializedName( "legs")
        val legs: List<LegsItem?> ,

        @SerializedName( "warnings")
        val warnings: List<Any?> ,

        @SerializedName( "bounds")
        val bounds: Bounds ,

        @SerializedName( "overview_polyline")
        val overviewPolyline: OverviewPolyline ,

        @SerializedName( "waypoint_order")
        val waypointOrder: List<Any?>
)

data class Duration(

        @SerializedName( "text")
        val text: String ,

        @SerializedName( "value")
        val value: Int
)
data class EndLocation(

        @SerializedName( "lng")
        val lng: Double ,

        @SerializedName( "lat")
        val lat: Double
)
data class LegsItem(

        @SerializedName( "duration")
        val duration: Duration ,

        @SerializedName( "start_location")
        val startLocation: StartLocation ,

        @SerializedName( "distance")
        val distance: Distance ,

        @SerializedName( "start_address")
        val startAddress: String ,

        @SerializedName( "end_location")
        val endLocation: EndLocation ,

        @SerializedName( "end_address")
        val endAddress: String ,

        @SerializedName( "via_waypoint")
        val viaWaypoint: List<Any?> ,

        @SerializedName( "steps")
        val steps: List<StepsItem?> ,

        @SerializedName( "traffic_speed_entry")
        val trafficSpeedEntry: List<Any?>
)

data class StartLocation(

        @SerializedName( "lng")
        val lng: Double ,

        @SerializedName( "lat")
        val lat: Double
)
