package com.example.app.repository;

import com.example.app.model.GpsData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GpsRepository extends CrudRepository<GpsData,Long> {
    Iterable<GpsData> findByUsrIDAndTripID(Long usrID,Long tripID);

    @Transactional
    @Query("SELECT MAX(tripID) FROM GpsData WHERE usrID=?1")
    Long maxTripId(Long usrId);

}
