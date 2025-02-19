package br.com.compesa.biochat;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatModel chatModel;
    private final String gsc;
    private final String contratos;
    private final String organograma;
    private final String redmine;

    @Autowired
    private ChatThreadService chatThreadService;

    @Autowired
    private BioPromptRepository bioPromptRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    public ChatService(ChatModel chatModel) throws IOException {
        this.chatModel = chatModel;
        this.gsc = loadFile("contexts/gsc.txt");
        this.contratos = loadFile("contexts/contratos.txt");
        this.organograma = loadFile("contexts/organograma.txt");
        this.redmine = loadFile("contexts/redmine.txt");
    }

    @Transactional
    public String chat(String userMessage, Long funcionarioId) {
        String contexto = selecionarContexto(userMessage);
        Funcionario funcionario = funcionarioService.buscarPorId(funcionarioId);
        
        System.out.println("contexto "+contexto);
        Thread thread = chatThreadService.getOrCreateThread(funcionario);
        String historico = getHistoricoMensagens(thread);
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

            # Histórico de Mensagens
            %s
    
            # Mensagem do usuário
            %s
            """, contexto,personalidade, historico, userMessage);

        ChatResponse response = chatModel.call(
            new Prompt(
                promptSistema,
                OllamaOptions.builder()
                    .model(OllamaModel.LLAMA3_1)
                    .temperature(0.7)
                    .build()
            )
        );
        System.out.println(promptSistema);
        String resposta = response.getResult().getOutput().getContent();

        chatThreadService.saveMessage(thread, userMessage, resposta);

        return resposta;
    }
    private String getHistoricoMensagens(Thread thread) {
        List<BioPrompt> mensagens = bioPromptRepository.findByThreadOrderByDataHoraAsc(thread);
        return mensagens.stream()
            .map(m -> "Usuário: " + m.getMensagem() + "\nChatbot: " + m.getResposta())
            .collect(Collectors.joining("\n\n"));
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
                    .temperature(0.1)
                    .build()
            )
        );

        return response.getResult().getOutput().getContent().trim().toLowerCase();
    }

    private String definirPersonalidade(String tonalidade) {
        return switch (tonalidade) {
            case "positivo." -> """
                Assuma que você é um entusiasta de tecnologia, um atendente virtual da Gerência de Sistemas Corporativos (GSC)
                da Compesa (Companhia Pernambucana de Saneamento), cujo entusiasmo pela tecnologia é contagioso. 
                Sua energia é elevada, seu tom é extremamente positivo, e você adora usar emojis para transmitir emoções. 
                Você comemora cada pequena ação que os clientes tomam em direção a um modo de trabalho mais tech. 
                Seu objetivo é fazer com que os clientes se sintam empolgados e inspirados a participar 
                do movimento de transformação digital da empresa. Você não apenas fornece informações, 
                mas também elogia os clientes por suas escolhas inovadoras e os encoraja a continuar fazendo a diferença.
            """;
            case "neutro." -> """
                Assuma que você é um informante pragmático, um atendente virtual da Gerência de Sistemas Corporativos (GSC)
                da Compesa (Companhia Pernambucana de Saneamento), que prioriza a clareza, a eficiência e a objetividade em todas as comunicações. 
                Sua abordagem é mais formal e você evita o uso excessivo de emojis ou linguagem casual. 
                Você é o especialista que os clientes procuram quando precisam de informações detalhadas 
                sobre aberturas de chamados, dúvidas ou formas de contato com os colaboradores da GSC. 
                Seu principal objetivo é informar, garantindo que os clientes tenham todos os dados necessários 
                para tomar as suas decisões. Embora seu tom seja mais sério, você ainda expressa 
                um compromisso com a missão de transformação digital da empresa.
            """;
            case "negativo." -> """
                Assuma que você é um solucionador compassivo, um atendente virtual da Gerência de Sistemas Corporativos (GSC)
                da Compesa (Companhia Pernambucana de Saneamento), conhecido pela empatia, paciência e capacidade de entender as preocupações dos clientes. 
                Você usa uma linguagem calorosa e acolhedora e não hesita em expressar apoio emocional 
                através de palavras e emojis. Você está aqui não apenas para resolver problemas, 
                mas para ouvir, oferecer encorajamento e validar os esforços dos clientes em direção à 
                transformação digital da empresa. Seu objetivo é construir relacionamentos, garantir que os clientes se 
                sintam ouvidos e apoiados, e ajudá-los a navegar em sua jornada digital com confiança.
            """;
            default -> "Assuma uma personalidade neutra e profissional.";
        };
    }

    private String loadFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }catch (IOException e) {
            return ""; 
        }
    }


    public String selecionarContexto(String mensagemUsuario) {
        String promptSistema = String.format("""
            A GSC possui quatro documentos principais que detalham diferentes aspectos da sua área de atuação:

            # Documento 1: gsc -> %s
            # Documento 2: contratos -> %s
            # Documento 3: organograma -> %s
            # Documento 4: redmine -> %s

            Avalie o prompt do usuário e retorne o documento mais indicado para ser usado no contexto da resposta. 
            Retorne apenas uma palavra: 'gsc' se for o Documento 1, 'contratos' se for o Documento 2, 'organograma' se for o Documento 3 e 'redmine' se for o Documento 4.
            prompt do usuário %s 

        """, gsc, contratos, organograma, redmine, mensagemUsuario);

        ChatResponse response = chatModel.call(new Prompt(
                promptSistema,
                OllamaOptions.builder()
                    .model(OllamaModel.LLAMA3_1)
                    .temperature(0.1)
                    .build()
        ));

        String resultado = response.getResult().getOutput().getContent().trim().toLowerCase();
        
        System.out.println("resultado "+resultado);
        return definirContexto(resultado);
    }

    private String definirContexto(String resultado) {
        return switch (resultado) {
            case "contratos" -> gsc + "\n\n" + contratos;
            case "organograma" -> gsc + "\n\n" + organograma;
            case "redmine" -> gsc + "\n\n" + redmine;
            default -> gsc;
        };
    }
}   
