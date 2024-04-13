package ui;

import exception.ResponseException;
import model.AuthInfo;
import model.GameInfo;

import java.util.*;
import java.util.stream.Collectors;

import static ui.EscapeSequences.*;

public class ChessGameClient {
    private AuthInfo authInfo;
    private HashMap<Integer, GameInfo> gameList = new HashMap<>();
    private ServerFacade server;
    private String url;
    private boolean serverRunning;
    private State state = State.LOGGEDOUT;
    public GameInfo gameInfo;


    public ChessGameClient() {
        server = new ServerFacade("http://localhost:8080");
        this.url = "http://localhost:8080";
    }

    public static void main(String[] args) throws ResponseException {
        var newClient = new ChessGameClient();
        newClient.run();
    }

    public String parser(String in) throws ResponseException {
        String[] parts = in.toLowerCase().trim().split("\\s+");
        String command = parts.length > 0 ? parts[0] : "help";
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);

        return switch (command) {
            case "quit" -> quit();
            case "register" -> register(arguments[0], arguments[1], arguments[2]);
            case "login" -> login(arguments[0], arguments[1]);
            case "logout" -> logout();
            case "list" -> listGames();
            case "create" -> arguments.length < 1 ? "Missing arguments. Use: create <game name>" : createGame(String.join(" ", arguments));
            case "join" -> arguments.length < 1 ? "Missing arguments. Use: join <game id> [white|black|<empty>]": joinGame(arguments[0], arguments.length < 2 ? null : arguments[1]);
            case "observe" -> arguments.length < 1 ? "Missing arguments. Use: observe <game id>" : observeGame(arguments[0]);
            case "help" -> help();
            default -> help();
        };

    }

    private void run() throws ResponseException {
        Scanner scan = new Scanner(System.in);
        System.out.println(SET_TEXT_COLOR_BLUE);
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "Welcome to CLI-Chess :)");
        System.out.println(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW);
        System.out.println("Input 'help' for a list of commands or 'quit' to exit.");
        serverRunning = true;
        while (serverRunning) {
            System.out.print("$>");
            var in = scan.nextLine();
            var out = parser(in);

            System.out.println(out);
        }
    }

    public String help() {
        StringBuilder helpMessage = new StringBuilder("Available commands:\n");

        if (state == State.LOGGEDIN) {
            helpMessage.append("- Create <name> - Create new game\n")
                    .append("- List - List all games\n")
                    .append("- Join <id> [white-black-<empty>] - Join game\n")
                    .append("- Observe <id> - Observe a game\n")
                    .append("- Logout - Log out of account\n")
                    .append("- Quit - Exit CLI-Chess\n")
                    .append("- Help - Display help message\n");
        } else {
            helpMessage.append("- Register <username> <password> <email> - Register new account\n")
                    .append("- Login <username> <password> - Log in an account\n")
                    .append("- Quit - Exit CLI-Chess\n")
                    .append("- Help - Display help message\n");
        }

        return helpMessage.toString();
    }


    public String quit() {
        if (this.state == State.LOGGEDIN){
            this.logout();
        }
        this.state = State.LOGGEDOUT;
        this.serverRunning = false;
        return "Exiting CLI-Chess. Come back soon!";
    }

    public String register(String username, String password, String email) {
        try {
            AuthInfo user = server.register(username, password, email);
            authInfo = user;
            state = State.LOGGEDIN;
            return user.username() + " is now registered for Chess!";
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String username, String password) {
        try {
            AuthInfo newUser = server.login(username, password);
            authInfo = newUser;
            state = State.LOGGEDIN;
            return String.format("%s logged in successfully!", newUser.username());
        } catch (ResponseException ex) {
            return "401".equals(ex.StatusCode()) ? "Invalid username or password." : ex.getMessage();
        }
    }

    public String logout() {
        try {
            server.logout(authInfo.authToken());
            authInfo = null;
            state = State.LOGGEDOUT;
            return "Logged out successfully!";
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String gameName) {
        try {
            var newGame = server.createGame(authInfo.authToken(), gameName);
            return newGame.gameName() + " Game created successfully!";
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String listGames() {
        if (this.state != State.LOGGEDIN) {
            return "Login to view games";
        }

        try {
            var gamesResponse = server.listGames(authInfo.authToken());
            if (gamesResponse == null || gamesResponse.games().isEmpty()) {
                return "No games being played :(";
            }

            StringBuilder output = new StringBuilder("List of Games:\n");
            List<GameInfo> sortedGames = gamesResponse.games().stream()
                    .sorted(Comparator.comparingInt(GameInfo::gameID))
                    .collect(Collectors.toList());

            for (int i = 0; i < sortedGames.size(); i++) {
                GameInfo game = sortedGames.get(i);
                String gameDetails = formatGameDetails(i, game);
                output.append(gameDetails);
            }

            return output.toString();
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String formatGameDetails(int index, GameInfo game) {
        return String.format("%s%d. gameName: %s%s, whiteUsername: %s%s, blackUsername: %s%s\n",
                EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_YELLOW,
                index + 1,
                EscapeSequences.SET_TEXT_ITALIC + SET_TEXT_COLOR_BLUE + game.gameName() + EscapeSequences.RESET_TEXT_ITALIC,
                EscapeSequences.SET_TEXT_COLOR_WHITE,
                EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_GREEN + game.whiteUsername() + EscapeSequences.RESET_TEXT_COLOR,
                EscapeSequences.SET_TEXT_BOLD,
                EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_RED + game.blackUsername() + EscapeSequences.RESET_TEXT_COLOR,
                EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }


    public String joinGame(String gameIndex, String color) {
        if (this.state == State.LOGGEDOUT) {
            return "You must login to join a game\n";
        }

        if (!color.equalsIgnoreCase("white") && !color.equalsIgnoreCase("black") && !color.equalsIgnoreCase("")) {
            return "Choose a team: 'white', 'black', or don't type anything :)\n";
        }

        int index;

        try {
            index = Integer.parseInt(gameIndex);
        } catch (NumberFormatException e) {
            return "Invalid game ID.\n";
        }

        try {
            var gamesResponse = server.listGames(authInfo.authToken());
            List<GameInfo> gameList = new ArrayList<>(gamesResponse.games());

            if (index <= 0 || index > gameList.size()) {
                return "Invalid game ID.\n";
            }

            int gameID = gameList.get(index - 1).gameID();
//            String displayColor = (color == null || color.isEmpty()) ? "white" : color;

//            return new LoadGameBoard(gameInfo).displayGame(displayColor);

            this.gameInfo = server.joinGame(authInfo.authToken(), gameID, color);
            GameInfo game = gameList.get(index -1);
            new LoadGameBoard(game, this.url, this.authInfo, color).startGame();
            return "";

        } catch (ResponseException e) {
            return e.getMessage();
        }



    }

    public String observeGame(String gameIndex) {
        if (this.state == State.LOGGEDOUT) {
            return "You must be logged in to observe a game.\n";
        }
        int index;
        try {
            index = Integer.parseInt(gameIndex);
        } catch (NumberFormatException ex) {
            return "Invalid game ID.\n";
        }

        try {
            var gamesResponse = server.listGames(authInfo.authToken());
            List<GameInfo> gameList = new ArrayList<>(gamesResponse.games());

            if (index <= 0 || index > gameList.size()) {
                return "Invalid game ID.\n";
            }

            int gameID = gameList.get(index - 1).gameID();
            this.gameInfo = server.joinGame(authInfo.authToken(), gameID, null);
           // return new LoadGameBoard(gameInfo).displayGame("");
            GameInfo game = gameList.get(index -1);
            new LoadGameBoard(game, this.url, this.authInfo, null).startGame();
            return "";

        } catch (ResponseException e) {
            return e.getMessage();
        }
    }



}
