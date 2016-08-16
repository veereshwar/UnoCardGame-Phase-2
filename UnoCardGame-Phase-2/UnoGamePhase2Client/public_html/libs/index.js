$(function () {
    

    $("#create").on("singletap", function () {
        var gameTitle = $("#gameTitle").val();
        var players = $("#maxplayers").val();
        $("#gameTitle").empty();
        $("#capacity").empty();
        $.post("http://localhost:8080/UnoGamePhase2/api/game/creategame", {gname:gameTitle, maxply: players })
                .done(function (result) {
                    $("#waitGameId").text(result.gameId);
                    $("#waitGameTitle").text(result.gameTitle);
                    $("#waitGameCapacity").text(result.maxplayers);
                    $.UIGoToArticle("#waitGame");
                });
    });
    
    $("#refreshBtn").on("singletap", function () {
        var gameId = $("#waitGameId").text();
        $.getJSON("http://localhost:8080/UnoGamePhase2/api/game/Get/Count/" + gameId).done(function (result) {
            $("#playerAmount").text(result.amount);
        });
    });
    
    $("#gameStartBtn").on("singletap", function () {
        var gameId = $("#waitGameId").text();
        if ($("#playerAmount").text() <= 1) {
            alert("Can't Start the Game");
            return;
        }
        $.post("http://localhost:8080/UnoGamePhase2/api/game/start",{gid: gameId}).done(function (result) {
            $("#discrad").attr("src", "http://localhost:8080/UnoGamePhase2/img/" + result[0].img2);
            var playerTemplate = Handlebars.compile($("#playerTemplate").html());
            for (var i = 1; i < result.length; i++) {
                $("#playerList").append(playerTemplate(result[i]));
            }
            $.UIGoToArticle("#gameTable");
        });
    });
});