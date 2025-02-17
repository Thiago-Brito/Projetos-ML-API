package br.com.compesa.biochat;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/chat")
public class ChatController {
     private final ChatService chatService;


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
        
    }
    @GetMapping()
    public String chat(@RequestParam String userMessage) {
        return chatService.chat(userMessage);
    }

    
}
