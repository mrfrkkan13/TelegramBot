package com.example.demoBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.Set;
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${spring.telegram.bot.username}")
    private String botUsername;

    @Value("${spring.telegram.bot.token}")
    private String botToken;

    private Set<Long> chatIdSet = new HashSet<>();

    @Override
    public String getBotUsername() {
        return "demoTelegramBotExample_bot";
    }

    @Override
    public String getBotToken() {
        return "7018852726:AAFGxEMGRKUQmyg4r23sRH9J3DawqpaKAEk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            System.out.println(messageText);
            if("/start".equals(messageText)){
                chatIdSet.add(chatId);
                System.out.println(chatId);
                String response = processMessage(messageText);
            }else{
                String response = processMessage(messageText);
                try {
                    execute(new SendMessage(chatId.toString(), response));
                }catch (TelegramApiException e){
                    e.printStackTrace();
                }
            }
        }
    }
    private void sendMessage(long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(messageText);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String processMessage(String messageText) {
        String response;

        // Example: If the user sends "Hello", respond with "Hi there!"
        if (messageText.equalsIgnoreCase("Hello")) {
            response = "Hi there!";
        } else if (messageText.equalsIgnoreCase("How are you?")) {
            response = "I'm just a bot, but I'm here to help!";
        } else {
            response = "I'm sorry, I didn't understand your message.";
        }

        return response;
    }

    @Scheduled(cron = "0 5 * * * *")
    public void notificationText() {
        Set<Long> chatIdList = chatIdSet;

        for (Long chatId : chatIdList) {
            try {
                execute(new SendMessage(chatId.toString(), "Bu bir deneme mesajıdır!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
