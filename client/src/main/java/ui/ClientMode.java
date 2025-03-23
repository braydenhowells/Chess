package ui;

// this will allow us to easily change between what kind of client we need
// preLogin, postLogin, and game clients will all have these 2 methods
public interface ClientMode {
    ClientMode eval(String input);
    String help();
}
