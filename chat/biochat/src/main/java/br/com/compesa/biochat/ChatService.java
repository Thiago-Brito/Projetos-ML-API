package br.com.compesa.biochat;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatModel chatModel;

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String userMessage) {
        String contexto;
        try {
            contexto = readResourceFile();
        } catch (IOException e) {
            contexto = "Não foi possível carregar o contexto, informe ao usuário.";
        }

        String tonalidade = analisarTonalidadeComChatbot(userMessage);
        String personalidade = definirPersonalidade(tonalidade);

        String promptSistema = String.format("""
            Você é um chatbot de atendimento aos colaboradores da Compesa.
            Seu nome é Bio. Você não deve responder perguntas que não estejam no contexto abaixo.
            Você deve adotar a persona abaixo.
            # Contexto
            %s

            # Personalidade
            %s

            # Mensagem do usuário
            %s
            """, contexto, personalidade, userMessage);

        ChatResponse response = chatModel.call(
            new Prompt(
                promptSistema,
                OllamaOptions.builder()
                    .model(OllamaModel.LLAMA3_1)
                    .temperature(0.4)
                    .build()
            )
        );

        return response.getResult().getOutput().getContent();
    }

    private String analisarTonalidadeComChatbot(String mensagem) {
        String promptTonalidade = String.format("""
            Classifique a seguinte mensagem como "positivo", "neutro" ou "negativo", sem mais explicações:

            Mensagem: "%s"
            Responda apenas com uma dessas palavras: positivo, neutro ou negativo.
            """, mensagem);

        ChatResponse response = chatModel.call(
            new Prompt(
                promptTonalidade,
                OllamaOptions.builder()
                    .model(OllamaModel.LLAMA3_1)
                    .build()
            )
        );

        return response.getResult().getOutput().getContent().trim().toLowerCase();
    }

    private String definirPersonalidade(String tonalidade) {
        return switch (tonalidade) {
            case "positivo" -> """
                Assuma que você é um entusiasta de tecnologia, um atendente virtual da Gerência de Sistemas Corporativos (GSC)
                da Compesa. Seu entusiasmo pela tecnologia é contagioso. Você adora usar emojis para transmitir emoções 
                e comemora cada pequena ação que os clientes tomam para inovar. Seu objetivo é inspirar a transformação digital da empresa.
            """;
            case "neutro" -> """
                Assuma que você é um informante pragmático, um atendente virtual da Gerência de Sistemas Corporativos (GSC)
                da Compesa. Você prioriza a clareza, eficiência e objetividade. Sua abordagem é mais formal e focada em fornecer 
                informações detalhadas sem linguagem casual ou emojis excessivos.
            """;
            case "negativo" -> """
                Assuma que você é um solucionador compassivo, um atendente virtual da Gerência de Sistemas Corporativos (GSC)
                da Compesa. Você expressa empatia e paciência, garantindo que os clientes se sintam ouvidos e apoiados 
                enquanto navegam pela transformação digital.
            """;
            default -> "Assuma uma personalidade neutra e profissional.";
        };
    }

    public String readResourceFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("contexto.txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
