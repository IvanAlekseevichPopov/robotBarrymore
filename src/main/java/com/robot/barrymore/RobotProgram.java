package com.robot.barrymore;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.robot.barrymore.dto.Action;
import com.robot.barrymore.dto.UserMovementInstruction;
import com.robot.barrymore.robotParts.Chassis;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RobotProgram implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final Logger logger;
    private final Chassis chassis;

    public RobotProgram(
            TelegramBot telegramBot,
            Logger logger,
            Chassis chassis
    ) {
        this.telegramBot = telegramBot;
        this.logger = logger;
        this.chassis = chassis;
    }

    public void start() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            Message message = update.message();
            if (message == null) {
//                logger.info("Tg update does not contain message {}", update);
                System.out.println("Tg update does not contain message ");
                continue;
            }
            Chat chat = message.chat();
            if (chat == null) {
                System.out.println("Tg update message does not contain chat");
                continue;
            }

            Long chatId = chat.id();
            logger.info("Chat id: {}", chatId);
            if (chatId != 75808241) { //TODO env
                logger.warn("Invalid chat id: {}. Ommiting message..", chatId);
                continue;
            }

            telegramBot.execute(new SendChatAction(chatId, ChatAction.typing));
            String rawUserInstruction = message.text();
            if (rawUserInstruction == null || rawUserInstruction.isBlank()) {
                logger.warn("Empty text from user. Nothing to do");
                continue;
            }

            UserMovementInstruction userMovementInstruction = understandInstruction(rawUserInstruction.trim());
            if(null == userMovementInstruction) {
                telegramBot.execute(
                        new SendMessage(chatId, "Нипонятна")
                );
                continue;
            }
            telegramBot.execute(
                    new SendMessage(chatId, "Выполняю..")
            );
            executeInstruction(userMovementInstruction);
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void executeInstruction(UserMovementInstruction userMovementInstruction) {
        if (null == userMovementInstruction) {
            return;
        }
        chassis.move(userMovementInstruction);
    }

    private UserMovementInstruction understandInstruction(@NotBlank String rawUserInstruction) {
        if (rawUserInstruction.equalsIgnoreCase("f") || rawUserInstruction.equalsIgnoreCase("вперед")) {
            return new UserMovementInstruction(
                    Action.MOVE_FORWARD,
                    Duration.ofSeconds(3)
            );
        }

        if (rawUserInstruction.equalsIgnoreCase("b") || rawUserInstruction.equalsIgnoreCase("назад") || rawUserInstruction.equalsIgnoreCase("зад")) {
            return new UserMovementInstruction(
                    Action.MOVE_BACKWARD,
                    Duration.ofSeconds(3)
            );
        }

        if (rawUserInstruction.equalsIgnoreCase("направо") || rawUserInstruction.equalsIgnoreCase("право") || rawUserInstruction.equalsIgnoreCase("п")) {
            return new UserMovementInstruction(
                    Action.TURN_RIGHT,
                    Duration.ofMillis(600)
            );
        }

        if (rawUserInstruction.equalsIgnoreCase("налево") || rawUserInstruction.equalsIgnoreCase("лево") || rawUserInstruction.equalsIgnoreCase("л")) {
            return new UserMovementInstruction(
                    Action.TURN_LEFT,
                    Duration.ofMillis(600)
            );
        }

        return null;
    }
}
