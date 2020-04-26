package com.app.parkingpal.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Check for reference
 * https://developers.google.com/maps/documentation/directions/intro#ExampleRequests
 */
public class DirectionsRequest {

    private String url = "https://maps.googleapis.com/maps/api/directions/json?";
    private String origin;
    private String destination;
    private String apiKey;

    private DirectionsRequest(DirectionsRequestBuilder directionsRequestBuilder) {
        this.origin = directionsRequestBuilder.origin;
        this.destination = directionsRequestBuilder.destination;
        this.apiKey = directionsRequestBuilder.apiKey;
    }

    public String getUrl(){
        return url + origin + "&" + destination + "&" + apiKey;
    }

    public static DirectionsRequestBuilder builder(){
        return new DirectionsRequestBuilder();
    }
    /**
     *
     * Builder design pattern with: <b>Forced ordered chain</b>
     */
    public static class DirectionsRequestBuilder{
        private String origin;
        private String destination;
        private String apiKey;

        public DirectionsRequest build(){
            return new DirectionsRequest(this);
        }
        /**
         *
         * @param origin LatLng of the user position
         * @return DestinationBuilder
         */
        public DestinationBuilder origin(LatLng origin){
            this.origin = String.format("origin=%s,%s",origin.latitude,origin.longitude);
            return new DestinationBuilder();
        }

        public class DestinationBuilder{
            private String destination;
            /**
             *
             * @param destination LatLng of the destination position
             * @return ApiKeyBuilder
             */
            public ApiKeyBuilder destination(LatLng destination){
                this.destination = String.format("destination=%s,%s",destination.latitude,destination.longitude);
                DirectionsRequestBuilder.this.destination = this.destination;
                return new ApiKeyBuilder();
            }


        }

        public class ApiKeyBuilder{
            private String apiKey;
            /**
             *
             * @param apiKey Google API Key
             * @return DirectionsRequestBuilder
             */
            public DirectionsRequestBuilder apiKey(String apiKey){
                this.apiKey = "key="+apiKey;
                DirectionsRequestBuilder.this.apiKey = this.apiKey;
                return DirectionsRequestBuilder.this;
            }
        }

    }
}
