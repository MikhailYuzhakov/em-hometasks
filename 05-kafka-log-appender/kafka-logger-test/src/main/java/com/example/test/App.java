package com.example.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main( String[] args ) throws InterruptedException {
        System.out.println( "Starting application..." );

        for (int i = 0; i < 10; i++) {
            logger.info("This is log message number {}", i);
            Thread.sleep(500);
        }

        logger.error("This is an error message");
        System.out.println("Finished.");
    }
}