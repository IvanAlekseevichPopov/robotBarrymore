package com.robot.barrymore;

//import jline.console.ConsoleReader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
//@EnableScheduling
//@ComponentScan({"com.sales.bot", "springadmin"})
public class BotApplication implements CommandLineRunner {
    private final RobotProgram robotProgram;

    public BotApplication(RobotProgram robotProgram) {
        this.robotProgram = robotProgram;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("main started");
        SpringApplication.run(BotApplication.class, args);
        System.out.println("main finished");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("starting command");
        robotProgram.start();
        System.out.println("finishing command");
    }
}
