package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers
			Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
			Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			Game game1 = new Game(LocalDateTime.now());
			Game game2 = new Game(LocalDateTime.now().plusHours(1));
			Game game3 = new Game(LocalDateTime.now().plusHours(2));
			Game game4 = new Game(LocalDateTime.now().plusHours(3));
			Game game5 = new Game(LocalDateTime.now().plusHours(4));
			Game game6 = new Game(LocalDateTime.now().plusHours(5));


			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);


            GamePlayer gameplayer1 = new GamePlayer(LocalDateTime.now(), player1, game1);
            gamePlayerRepository.save(gameplayer1);
            GamePlayer gameplayer2 = new GamePlayer(LocalDateTime.now(), player2, game1);
            gamePlayerRepository.save(gameplayer2);

            GamePlayer gameplayer3 = new GamePlayer(LocalDateTime.now(), player2, game2);
            gamePlayerRepository.save(gameplayer3);
            GamePlayer gameplayer4 = new GamePlayer(LocalDateTime.now(), player2, game2);
            gamePlayerRepository.save(gameplayer4);

            GamePlayer gameplayer5 = new GamePlayer(LocalDateTime.now(), player3, game3);
            gamePlayerRepository.save(gameplayer5);
            GamePlayer gameplayer6 = new GamePlayer(LocalDateTime.now(), player1, game3);
            gamePlayerRepository.save(gameplayer6);

            GamePlayer gameplayer7 = new GamePlayer(LocalDateTime.now(), player1, game4);
            gamePlayerRepository.save(gameplayer7);
            GamePlayer gameplayer8 = new GamePlayer(LocalDateTime.now(), player2, game4);
            gamePlayerRepository.save(gameplayer8);


			Ship ship1 = new Ship("Patrol Boat",gameplayer1, Arrays.asList("B4", "B5"));
			shipRepository.save(ship1);
            Ship ship2 = new Ship ( "Destroyer",gameplayer2, Arrays.asList("H2", "H3", "H4"));
            shipRepository.save(ship2);
            Ship ship3 = new Ship ( "Submarine",gameplayer3, Arrays.asList("E1", "F1", "G1"));
            shipRepository.save(ship3);



            Salvo salvo1 = new Salvo(1, gameplayer1, Arrays.asList("C3", "D4"));
            salvoRepository.save(salvo1);
			Salvo salvo2 = new Salvo(1, gameplayer2, Arrays.asList("D7", "C4"));
			salvoRepository.save(salvo2);
			Salvo salvo3 = new Salvo(2, gameplayer1, Arrays.asList("F5", "F6"));
			salvoRepository.save(salvo3);
			Salvo salvo4 = new Salvo(2, gameplayer2, Arrays.asList("B2", "B4"));
			salvoRepository.save(salvo4);


			Score score1 = new Score(LocalDateTime.now(), player1, game1, 0.5);
			scoreRepository.save(score1);
			Score score2 = new Score(LocalDateTime.now(), player2, game1, 0.5);
			scoreRepository.save(score1);
			Score score3 = new Score(LocalDateTime.now(), player3, game2, 1.0);
			scoreRepository.save(score1);



		};
	}

	}



@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName -> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("PLAYER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private Object HttpServletResponse;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/web/**").permitAll()
		//		.antMatchers("/login").permitAll()
				.antMatchers("/**").hasAuthority("PLAYER")
		;
		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login")
		;
		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}


	}



