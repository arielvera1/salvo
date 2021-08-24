package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/games")
   public Map<String, Object> getControllerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("games",gameRepository.findAll().stream().map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));
       return dto;

    }
    @RequestMapping("/game_view/{gp}")
    public Map<String, Object> getGameview(@PathVariable Long gp) {
        GamePlayer gpActual=gamePlayerRepository.findById(gp).get();
        return gpActual.makeGameViewDTO();

    }



}




