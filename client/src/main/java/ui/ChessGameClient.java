package ui;

import java.util.Arrays;
import java.util.Scanner;

import chess.ChessGame;
import model.AuthInfo;
public class ChessGameClient {
    private Server server;
    private String url;
    private boolean serverRunning;
    private Status status = Status.LoggedOut;
    public ChessGameClient() {
        server = new Server("http://localhost:1234");
        this.url = "http://localhost:1234";
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
//            case "register" -> register(arguments[0], arguments[1], arguments[2]);
//            case "login" -> login(arguments[0], arguments[1]);
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

        if (status == Status.LoggedIn) {
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

}
