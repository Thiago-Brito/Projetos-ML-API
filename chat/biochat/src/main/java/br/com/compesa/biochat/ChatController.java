package br.com.compesa.biochat;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.MediaType;



@RestController
@RequestMapping("/chat")
public class ChatController {
     private final ChatService chatService;
     private final FuncionarioService funcionarioService;

    public ChatController(ChatService chatService,FuncionarioService funcionarioService) {
        this.chatService = chatService;
        this.funcionarioService = funcionarioService;
        
    }
    /*@PostMapping()
     public String chat(@RequestParam Long funcionarioId, @RequestBody MessageDTO messageDTO) {
        return chatService.chat(messageDTO.getUserMessage(), funcionarioId);
    }*/

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStreaming(@RequestBody MessageDTO messageDTO, @RequestParam Long funcionarioId) {
        return chatService.chat(messageDTO.getUserMessage(), funcionarioId);
    }

    // Criar novo funcionário
    @PostMapping("/novo-funcionario")
    public Funcionario criarFuncionario(@RequestParam String nome, @RequestParam String email) {
        return funcionarioService.criarFuncionario(nome, email);
    }
    
}
