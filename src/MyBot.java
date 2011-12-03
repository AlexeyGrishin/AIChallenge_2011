import bot.brain.BrainBot;
import bot.brain.Strategy;
import bot2.Bot;

import java.io.IOException;

public class MyBot {

    /**
     * Main method executed by the game engine for starting the bot.
     *
     * @param args command line arguments
     *
     * @throws java.io.IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        new Bot().readSystemInput();
    }
}
