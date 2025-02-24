package br.com.compesa.biochat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@NoArgsConstructor  
@AllArgsConstructor 
public class MessageDTO {
    private String userMessage;
    private Long funcionarioId;

}
