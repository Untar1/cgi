package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {
    private String location = "upload-dir";

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String newLocation) {
        if (!newLocation.isEmpty()) {
            this.location = newLocation;
        }
    }
}
