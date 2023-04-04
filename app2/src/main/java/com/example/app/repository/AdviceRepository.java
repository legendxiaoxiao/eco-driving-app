package com.example.app.repository;

import com.example.app.model.Advice;
import org.springframework.data.repository.CrudRepository;

public interface AdviceRepository extends CrudRepository<Advice,Long> {
    Iterable<Advice> findAdviceByUsrIDAndTripID(Long usrID,Long tripID);


}
