package com.example.app.service;

import com.example.app.model.Advice;
import com.example.app.repository.AdviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdviceService {

    private final AdviceRepository repository;

    public Iterable<Advice> findAdviceByUsrIDAndTripID(Long userID, Long tripID){
        return repository.findAdviceByUsrIDAndTripID(userID,tripID);
    };

}
