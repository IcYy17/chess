package ui;

import exception.ResponseException;
import model.AuthInfo;

import java.util.Arrays;
import java.util.Scanner;


public class ChessGameClient {
    private AuthInfo authInfo;
    private ServerFac server;
    private String url;
    private boolean serverRunning;
    private State status = State.LOGGEDOUT;
    public ChessGameClient() {
        server = new ServerFac("http://localhost:8080");
        this.url = "http://localhost:8080";
    }
    public static void main(String[] args) {
        var newClient = new ChessGameClient();
        newClient.run();
    }

    public String parser(String in){
        String[] parts = in.toLowerCase().trim().split("\\s+");
        String command = parts.length > 0 ? parts[0] : "help";
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);

        return switch (command) {
            case "quit" -> quit();
            case "register" -> register(arguments[0], arguments[1], arguments[2]);
            case "login" -> login(arguments[0], arguments[1]);
//            case "logout" -> logout();
//            case "list" -> listGames();
//            case "create" -> createGame(Arrays.stream(arguments).collect(Collectors.joining(" ")));
//            case "join" -> joinGame(Integer.parseInt(arguments[0]), arguments.length > 1 ? arguments[1] : "");
//            case "observe" -> observeGame(Integer.parseInt(arguments[0]));
            case "help" -> help();
            default -> help();
        };

    }
    private void run(){
        Scanner scan = new Scanner(System.in);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE);
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "Welcome to CLI-Chess :)");
        System.out.println(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW);
        System.out.println("Input 'help' for a list of commands or 'quit' to exit.");
        serverRunning = true;
        while(serverRunning){
            System.out.print("$>");
            var in = scan.nextLine();
            var out = parser(in);

            System.out.println(out);
        }
    }
    public String help() {
        StringBuilder helpMessage = new StringBuilder("Available commands:\n");

        if (status == State.LOGGEDIN) {
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
        serverRunning = false;
        return "Exiting CLI-Chess. Come back soon!";
    }
    public String register(String username, String password, String email) {
        try {
            AuthInfo user = server.register(username, password, email);
            authInfo = user;
            status = State.LOGGEDIN;
            return user.username() + " is now registered for Chess!";
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String username, String password) {
        try {
            AuthInfo newUser = server.login(username, password);
            authInfo = newUser;
            status = State.LOGGEDIN;
            return String.format("%s logged in successfully!", newUser.username());
        } catch (ResponseException ex) {
            return "401".equals(ex.StatusCode()) ? "Invalid username or password." : ex.getMessage();
        }
    }

}
