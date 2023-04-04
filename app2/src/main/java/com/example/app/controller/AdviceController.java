package com.example.app.controller;

import com.example.app.service.AdviceService;
import com.example.app.model.Advice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/advice")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))


public class AdviceController {
        private final AdviceService service;
        @GetMapping("/getOneUserOneTripAdvice")
        public @ResponseBody Iterable<Advice> getOneUserOneTripAdvice(@RequestParam Long usrID, @RequestParam Long tripID ){
            return service.findAdviceByUsrIDAndTripID(usrID, tripID);
        }
}
