package br.com.compesa.biochat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatThreadService {
    @Autowired
    private ThreadRepository threadRepository;
    
    @Autowired
    private BioPromptRepository bioPromptRepository;
    
    public Thread getOrCreateThread(Funcionario funcionario) {
        Optional<Thread> threadOpt = threadRepository.findTopByFuncionarioOrderByDataCriacaoDesc(funcionario);
        return threadOpt.orElseGet(() -> {
            Thread newThread = new Thread();
            newThread.setFuncionario(funcionario);
            newThread.setDataCriacao(LocalDateTime.now());
            return threadRepository.save(newThread);
        });
    }
    
    public BioPrompt saveMessage(Thread thread, String mensagem, String resposta) {
        BioPrompt prompt = new BioPrompt();
        prompt.setThread(thread);
        prompt.setFuncionario(thread.getFuncionario());
        prompt.setMensagem(mensagem);
        prompt.setResposta(resposta);
        prompt.setDataHora(LocalDateTime.now());
        return bioPromptRepository.save(prompt);
    }

     public String getHistoricoMensagens(Thread thread) {
        List<BioPrompt> mensagens = bioPromptRepository.findByThreadOrderByDataHoraAsc(thread);
        if (mensagens.size() >= 2) {
            bioPromptRepository.delete(mensagens.get(0));
        }
        return mensagens.stream()
            .map(m -> "Usuário: " + m.getMensagem() + "\nChatbot: " + m.getResposta())
            .collect(Collectors.joining("\n\n"));
    }
}
