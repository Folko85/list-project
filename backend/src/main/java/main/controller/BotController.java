package main.controller;

import main.bot.MyToDoBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class BotController {

    private final MyToDoBot myBot;

    public BotController(MyToDoBot myBot) {
        this.myBot = myBot;
    }

    @PostMapping ("/bot")
    public BotApiMethod<?> onUpdateReceived (@RequestBody Update update){
        return  myBot.onWebhookUpdateReceived(update);
    }
}
