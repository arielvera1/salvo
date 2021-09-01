package com.codeoftheweb.salvo;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "gameID", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;
//  relacion score

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores;


    //constructor


    public Game() {
    }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    //setter y getter

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
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

    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("creationDate", this.getCreationDate());
        dto.put("gamePlayers", this.getGamePlayers().stream().map(gp->gp.makeGamePlayerDTO()).collect(Collectors.toList()));
        dto.put("scores",this
                .getGamePlayers()
                .stream()
                .map(gp -> {if(gp.getScore().isPresent()){return gp.getScore().get().makeScoreDTO();}
                else {return "";}}));
        return dto;
    }

}





