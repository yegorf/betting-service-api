package com.yegorf.bookmaker.controllers;

import com.yegorf.bookmaker.coefs.ResultCalculator;
import com.yegorf.bookmaker.dto.ResponseResult;
import com.yegorf.bookmaker.entities.Event;
import com.yegorf.bookmaker.entities.EventResult;
import com.yegorf.bookmaker.enums.EventStatus;
import com.yegorf.bookmaker.repos.BetRepo;
import com.yegorf.bookmaker.repos.EventRepo;
import com.yegorf.bookmaker.repos.EventResultRepo;
import com.yegorf.bookmaker.repos.UserRepo;
import com.yegorf.bookmaker.rusults_analis.WinningsPayer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequestMapping("/results")
public class ResultController {
    private final EventResultRepo eventResultRepo;
    private final EventRepo eventRepo;
    private final BetRepo betRepo;
    private final UserRepo userRepo;

    public ResultController(EventResultRepo eventResultRepo, EventRepo eventRepo, BetRepo betRepo, UserRepo userRepo) {
        this.eventResultRepo = eventResultRepo;
        this.eventRepo = eventRepo;
        this.betRepo = betRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/getEventResults")
    public HashSet<ResponseResult> getEventResults(@RequestParam Integer id) {
        ResultCalculator calculator = new ResultCalculator(betRepo, eventResultRepo);
        HashSet<ResponseResult> results = null;

        for (Event event : eventRepo.findAll()) {
            if (event.getId().equals(id)) {
                results = calculator.setSumCoef(event);
                break;
            }
        }

        assert results != null;
        return results;
    }

    @PostMapping("/setEventResult")
    public void setEventResult(@RequestParam int eventId,
                               @RequestParam int resultId) {
        Event event = eventRepo.findById(eventId);
        event.setActive(EventStatus.FINISHED.name());
        eventRepo.save(event);

        EventResult wonResult = null;
        for(EventResult result : eventResultRepo.findAllByEvent(event)) {
            if(result.getId() == resultId) {
                result.setWon(1);
                wonResult = result;
            } else {
                result.setWon(0);
            }
            eventResultRepo.save(result);
        }

        new WinningsPayer(betRepo, userRepo).pay(wonResult);
    }
}
