package ui;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class repl {
    private final preLoginClient client;

    public repl(preLoginClient client) {
        this.client = client;
    }

    public void run() {
        System.out.println("\uD83D\uDE08 Welcome to chess, boyo.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Thanks for playing!")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
