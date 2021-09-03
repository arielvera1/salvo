package com.codeoftheweb.salvo;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/games")
    public Map<String, Object> getControllerDTO(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            dto.put("player", playerRepository.findByUserName(authentication.getName()).makePlayerDTO());
        }
        dto.put("games", gameRepository.findAll().stream().map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));
        return dto;

    }

    @RequestMapping("/game_view/{gp}")
    public Map<String, Object> getGameview(@PathVariable Long gp) {
        GamePlayer gpActual = gamePlayerRepository.findById(gp).get();
        return gpActual.makeGameViewDTO();

    }

    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(email);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    @RequestMapping("/gameview/{nn}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long nn, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();

        if (playerRepository.findByUserName(authentication.getName()).getGamePlayers()
                .stream().anyMatch(gamePlayer1 -> gamePlayer1.getId().equals(nn))) {
            return new ResponseEntity<>(gamePlayer.makeGameViewDTO(), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(makeMap("error", 1), HttpStatus.FORBIDDEN);
        }

    }



   /* @PostMapping (path = "/games")
    public ResponseEntity<Map<String, CreatedDate>> createGame(@RequestParam Long Id) {
        if (game.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No game"), HttpStatus.FORBIDDEN);
        }
        Game game = gameRepository.findById(Id);
        if (game != null) {
            return new ResponseEntity<>(makeMap("error", "Game already exists"), HttpStatus.CONFLICT);
        }
        Game game1 = gameRepository.save(new Game(Id));
        return new ResponseEntity<>(makeMap("id", newGame.getId()), , HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    } */

    @PostMapping("/game")
    public ResponseEntity<Map<String, Object>> createGame(LocalDateTime creationDate, Authentication authentication) {

        if (!isGuest(authentication)) {
            Game newGame = new Game(LocalDateTime.now());
            gameRepository.save(newGame);
            Player auth = playerRepository.findByUserName(authentication.getName());
            GamePlayer gamePlayer = new GamePlayer(LocalDateTime.now(), auth, newGame);
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(makeMap("Error", "Debes iniciar sesión!"), HttpStatus.UNAUTHORIZED);
        }


    }


  /*
        if (!isGuest(authentication)) {
            Optional<Game> game = gameRepository.findById(nn);
            Player auth = playerRepository.findByUserName(authentication.getName());
            if (game.isPresent()) {
                if (game.get().getGamePlayers().size() < 2) {
                    if (game.get().getGamePlayers().stream().anyMatch(jg -> jg.getPlayerID().getId() != auth.getId())) {

                        GamePlayer gamePlayer = new GamePlayer(LocalDateTime.now(), auth, game.get());
                        gamePlayerRepository.save(gamePlayer);
                        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);

                    } else {

                        return new ResponseEntity<>(makeMap("Error", "No se puede unir a un mismo juego dos veces!"), HttpStatus.FORBIDDEN);
                    }

                } else {

                    return new ResponseEntity<>(makeMap("Error", "El juego esta completo!"), HttpStatus.FORBIDDEN);
                }

            } else {

                return new ResponseEntity<>(makeMap("Error", "El juego no existe!"), HttpStatus.NOT_FOUND);
            }

        } else {

            return new ResponseEntity<>(makeMap("Error", "Registrate para poder unirte al juego!"), HttpStatus.UNAUTHORIZED);
        }


    } */
}






