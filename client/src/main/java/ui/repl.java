package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class repl {
    private ClientMode mode;

    public repl(ClientMode startingMode) {
        this.mode = startingMode;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                ClientMode next = mode.eval(line);
                if (next == null) {
                    break;
                }
                mode = next;
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }

        System.out.println("\nGoodbye!");
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + ">>> " );
    }
}
