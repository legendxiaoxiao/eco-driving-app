package com.example.app.controller;

import com.example.app.model.GpsData;
import com.example.app.service.GpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/gps")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GpsController {

    private final GpsService service;

    @GetMapping("/getOneUserOneTripGps")
    public @ResponseBody Iterable<GpsData> getOneUserOneTripGps(@RequestParam Long usrID,@RequestParam Long tripID ){
        return service.findByUsrIDAndTripID(usrID, tripID);
    }

    @GetMapping("/maxTripId")
    public @ResponseBody Long maxTripId(@RequestParam Long usrID){
        return service.maxTripId(usrID);
    }

    @PostMapping("/upLoad")
    public @ResponseBody boolean upLoad(@RequestBody List<GpsData> gpsData) throws IOException {
        boolean flag=true;
        for (GpsData gd:gpsData){
            if (!service.insert(gd)){
                flag=false;
            }
        }
//        if (flag){
//            String[] idInfo = new String[]{usrID,tripID};
//            Process proc = Runtime.getRuntime().exec(arg,idInfo);
//        }
        return flag;
    }

    @GetMapping("/getUsrTripForAdvice")
    public @ResponseBody boolean callForAdvice(@RequestParam Long usrID,@RequestParam Long tripID)  {
        boolean flag2=true;
        flag2 = service.makeAdvice(usrID,tripID);
        return flag2;
    }

    @GetMapping("/getTripValue")
    public @ResponseBody Iterable<GpsData> getTripValue(@RequestParam Long usrID,@RequestParam Long tripID ){
        return service.findByUsrIDAndTripID(usrID, tripID);
    }

}
