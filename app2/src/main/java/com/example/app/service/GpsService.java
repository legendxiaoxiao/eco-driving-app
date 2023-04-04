package com.example.app.service;

import com.example.app.model.GpsData;
import com.example.app.repository.GpsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GpsService {
    private final GpsRepository repository;

    public Iterable<GpsData> findByUsrIDAndTripID(Long usrID,Long tripID){
        return repository.findByUsrIDAndTripID(usrID, tripID);
    }

    public Long maxTripId(Long usrID){
        return repository.maxTripId(usrID);
    }

    public boolean insert(GpsData data){
        repository.save(data);
        return true;
    }

    public boolean makeAdvice(Long usrID,Long tripID)  {

        final String pythonScriptPath =  "/Users/carlos/IdeaProjects/CallPython/pyScript/main.py";
        final String pythonPath = "/usr/bin/python3";
        String[] arg = new String[]{pythonPath,pythonScriptPath};
        String[] idInfo = new String[]{String.valueOf(usrID), String.valueOf(tripID)};
        try {
            Process proc = Runtime.getRuntime().exec(arg,idInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
