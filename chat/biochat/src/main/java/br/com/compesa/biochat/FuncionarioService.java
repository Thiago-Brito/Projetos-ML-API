package br.com.compesa.biochat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public Funcionario criarFuncionario(String nome, String email) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setDataContratacao(LocalDate.now()); // Define a data de contratação para hoje
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }
}
