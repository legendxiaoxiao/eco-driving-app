package com.example.app.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "advice")
public class Advice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long __id;
    private Long userID;
    private Long tripID;
    private String Index;
    private Long duration;
    private Double distance;
    private Double aver_speed;
    private Double std_acceleration;
    private Double Uniform_time_por;
    private Integer label;
    private Double consumption;
    private Integer Cluster;
    private Double score;
    private Double percentile;
    private String core_thing;

}
