package natanael.kanban.controller;

import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SimpleAiController {

    private final ChatClient chatClient;

    public SimpleAiController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    
    @GetMapping("/ai")
    public Map<String, String> generation(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        try {
            return Map.of("generation", chatClient.call(message));
        } catch (Exception e) {
            System.out.print(e);
            return null;
        }
    
    }
    
    /*
    @GetMapping("/ai")
    public Object generation(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        try {
            return chatClient.call(message);
        } catch (Exception e) {
            System.out.print(e);
            return null;
        }
    
    }
    */
}