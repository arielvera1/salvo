
package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String userName;

    @OneToMany(mappedBy = "playerID", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    // relacion score
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;


    //constructor
    public Player() {
    }

    public Player(String userName) {
        this.userName = userName;
    }

    //setter y getter


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGameID()).collect(Collectors.toList());


    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }


    public Optional<Score> getScore(Game game) {
        return this.getScores().stream().filter(sc -> sc.getGame().getId()==(game.getId())).findFirst();
    }

    }




