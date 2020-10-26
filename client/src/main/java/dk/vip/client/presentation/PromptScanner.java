package dk.vip.client.presentation;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PromptScanner implements IPromptReader {

    private Logger logger = Logger.getLogger(PromptScanner.class.getName());
    private boolean isRunning;
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void start() {
        isRunning = true;
        readLoop();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    private void readLoop() {
        new Thread(() -> {
            while (isRunning) {
                readLine();
            }
            scanner.close();
        }).start();
    }

    private void readLine() {
        if (scanner.hasNextLine()) {
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                isRunning = false;
            }
            logger.log(Level.INFO, "Testing read msg:" + query);
        }
    }

}