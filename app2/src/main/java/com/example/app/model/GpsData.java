package com.example.app.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "gps_data")
public class GpsData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long usrID;
    private Long tripID;
    private Double latitude;
    private Double longitude;
    private Double direct;
    private Double speed;
    private String gpstime;
    private String address;
}
