/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa42.Team9.Conn;

import Starter.GameEngine;
import Uno.model.Game;
import Uno.model.unoCard;
import Uno.model.unoPlayer;
import enums.Image;
import enums.Status;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author E0015359
 */
@RequestScoped
@Path("/game")
public class GameResource {

    @Inject
    private GameTable gameTable;

//    public static String playerId="";
//    public static UnoPlayer  player= null;
    @POST
    @Path("creategame")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGame(@FormParam("gname") String name, @FormParam("maxply") int maxp) {

        String gameTitle = name;
        String generatedID = UUID.randomUUID().toString().substring(0, 8);
        String gameUID = generatedID;

        Game game = new Game(gameUID, gameTitle, Status.GAME_WAITING, maxp);
        gameTable.getGametable().put(gameUID, game);

        System.out.print(">> Create the game：ID:" + gameUID + " TITLE:" + name);

        return Response.ok(game.toJson()).build();
    }

    @GET
    @Path("GET/gameList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameList() {
        System.out.print(">> Get the Game List");
        JsonArrayBuilder gameJsonArray = Json.createArrayBuilder();
        for (Map.Entry<String, Game> game : gameTable.getGametable().entrySet()) {
            gameJsonArray.add(game.getValue().toJson());
        }
        return Response.ok(gameJsonArray.build()).build();
                               
    }

    @POST
    @Path("POST/joinGame/{gameId}/{playerName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response joinGame(@PathParam("gameId") String gameId, @PathParam("playerName") String playerName) {
        UUID id = UUID.randomUUID();
        String playerId = id.toString().substring(0, 6);
        
        unoPlayer player = new unoPlayer(playerName, playerId);
        
        
        Game g = gameTable.getGametable().get(gameId);

        System.out.println(">> Player Max " + g.getmaxPlayers()+ "Current player " + g.getGamePlayers().size()  );
        if (g.getmaxPlayers() == g.getGamePlayers().size()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
       g.addPlayer(player);
        
        return Response.ok().build();
                
               
    }

    @GET
    @Path("Get/Count/{gameId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayerAmount(@PathParam("gameId") String gameId) {
        int amount = 0;
        Game g = gameTable.getGametable().get(gameId);
        amount = g.getGamePlayers().size();
        JsonObject json = Json.createObjectBuilder()
                .add("amount", amount)
                .build();
        return Response.ok(json).build();
                
                
    }

    @POST
    @Path("start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startGame(@FormParam("gid") String gameId) {
        Game g = gameTable.getGametable().get(gameId);
        GameEngine.initDeck(g.getGameDeck());
        GameEngine.initGame(g);
        JsonArrayBuilder gameInfoJsonArray = Json.createArrayBuilder();

        JsonObject gameJson = Json.createObjectBuilder()
                .add("img1", Image.BACK)
                .add("img2", g.getDicardPile().getImage())
                .build();

        gameInfoJsonArray.add(gameJson);

        for (unoPlayer p : g.getGamePlayers()) {
            JsonObject gameJson1 = Json.createObjectBuilder()
                    .add("name", p.getName())
                    .build();
            gameInfoJsonArray.add(gameJson1);
        }
        return Response.ok(gameInfoJsonArray.build()).build();
               
               
    }

    @GET
    @Path("GET/gameInfo/{gameId}/{playerName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameInfo(@PathParam("gameId") String gameId, @PathParam("playerName") String name) {
        Game game = gameTable.getGametable().get(gameId);
        System.out.println(game.getGameStatus());
        
        if (!game.getGameStatus().equals(Status.GAME_START)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();                 
                    
        } else {

            JsonArrayBuilder handCards = Json.createArrayBuilder();
            unoPlayer player = null;
            for (unoPlayer p : game.getGamePlayers()) {
                if (p.getName().equals(name)) {
                    player = p;
                }
            }
            for (unoCard c : player.getHandCards()) {
                JsonObject cards = Json.createObjectBuilder()
                        .add("card", c.getImage())
                        .build();
                handCards.add(cards);
            }
            return Response.ok(handCards.build()).build();
                    
                    
        }
    }
}
