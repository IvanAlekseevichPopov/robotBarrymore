package com.robot.barrymore.robotParts;

import com.robot.barrymore.dto.UserMovementInstruction;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Timer;

@Service
public class Chassis {
    private final static int PIN_LEFT_FORWARD = 21;
    private final static int PIN_LEFT_BACKWARD = 23;
    private final static int PIN_RIGHT_FORWARD = 2;
    private final static int PIN_RIGHT_BACKWARD = 20;

    private final static int DEFAULT_MAX_PWM = 1000;

    private final String bashCommand;
    private final Logger logger;

    public Chassis(
            @Value("#{environment.BASH_COMMAND}")
            String bashCommand,
            Logger logger
    ) {
        this.bashCommand = bashCommand;
        this.logger = logger;
        init();
    }


    public void move(UserMovementInstruction instruction) {
        switch (instruction.action()) {
            case TURN_LEFT -> turn(false, instruction.duration());
            case TURN_RIGHT -> turn(true, instruction.duration());
            case MOVE_FORWARD -> move(true, instruction.duration());
            case MOVE_BACKWARD -> move(false, instruction.duration());
        }
    }

    private void turn(boolean isRight, Duration duration) {
        try {
            if (isRight) {
                Runtime.getRuntime().exec(String.format("%s pwm %d %d", bashCommand, PIN_LEFT_FORWARD, DEFAULT_MAX_PWM));
                Runtime.getRuntime().exec(String.format("%s write %d 1", bashCommand, PIN_RIGHT_BACKWARD));
            } else {
                Runtime.getRuntime().exec(String.format("%s pwm %d %d", bashCommand, PIN_RIGHT_FORWARD, DEFAULT_MAX_PWM));
                Runtime.getRuntime().exec(String.format("%s write %d 1", bashCommand, PIN_LEFT_BACKWARD));
            }

            delayedStop(duration);
        } catch (IOException e) {
            logger.error("Exeption: {}", e.getMessage());
        }

    }

    private void move(boolean isForward, Duration duration) {
        System.out.println(String.format("%s pwm %d %d", bashCommand, PIN_LEFT_FORWARD, DEFAULT_MAX_PWM));
        try {
            if (isForward) {
                Runtime.getRuntime().exec(String.format("%s pwm %d %d", bashCommand, PIN_LEFT_FORWARD, DEFAULT_MAX_PWM));
                Runtime.getRuntime().exec(String.format("%s pwm %d %d", bashCommand, PIN_RIGHT_FORWARD, DEFAULT_MAX_PWM));
            } else {
                Runtime.getRuntime().exec(String.format("%s write %d 1", bashCommand, PIN_LEFT_BACKWARD));
                Runtime.getRuntime().exec(String.format("%s write %d 1", bashCommand, PIN_RIGHT_BACKWARD));
            }

            delayedStop(duration);

        } catch (IOException e) {
            logger.error("Exeption: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void init() {
        try {
            Runtime.getRuntime().exec(String.format("%s mode %d PWM", bashCommand, PIN_LEFT_FORWARD));
            Runtime.getRuntime().exec(String.format("%s mode %d OUT", bashCommand, PIN_LEFT_BACKWARD));
            Runtime.getRuntime().exec(String.format("%s mode %d PWM", bashCommand, PIN_RIGHT_FORWARD));
            Runtime.getRuntime().exec(String.format("%s mode %d OUT", bashCommand, PIN_RIGHT_BACKWARD));
        } catch (IOException e) {
            logger.error("init method exception: {}", e.getMessage());
        }

    }

    private void delayedStop(Duration duration) {
        new Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        stop();
                    }
                },
                duration.toMillis()
        );
    }

    private void stop() {
        try {
            Runtime.getRuntime().exec(String.format("%s pwm %d 0", bashCommand, PIN_LEFT_FORWARD));
            Runtime.getRuntime().exec(String.format("%s pwm %d 0", bashCommand, PIN_RIGHT_FORWARD));
            Runtime.getRuntime().exec(String.format("%s write %d 0", bashCommand, PIN_RIGHT_BACKWARD));
            Runtime.getRuntime().exec(String.format("%s write %d 0", bashCommand, PIN_LEFT_BACKWARD));
        } catch (IOException e) {
            logger.error("Stop method exception: {}", e.getMessage());
        }

    }
}
