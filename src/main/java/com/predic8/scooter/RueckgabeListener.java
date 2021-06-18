package com.predic8.scooter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predic8.scooter.model.RueckgabeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class RueckgabeListener {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    ObjectMapper om;

    @Autowired
    UserIdClient userIdClient;

    @Autowired
    BonusService bonusService;

    @KafkaListener(topics = "scooter.rueckgabe")
    public void rueckgabeListener(String rueckgabe) throws JsonProcessingException {
        RueckgabeDTO rueckgabeDTO = om.readValue(rueckgabe, RueckgabeDTO.class);

        log.info("Scooter " + rueckgabeDTO.getScooterId() + " wurde zurückgegeben.");

//        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:8083/scooter/" + rueckgabeDTO.getScooterId() + "/fahrt", String.class);
        ResponseEntity<String> userForScooter = userIdClient.getUserForScooter(rueckgabeDTO.getScooterId());

        log.info("UserID from fahrten service: " + userForScooter.getBody());

        bonusService.addBonusForUser(userForScooter.getBody());
    }
}