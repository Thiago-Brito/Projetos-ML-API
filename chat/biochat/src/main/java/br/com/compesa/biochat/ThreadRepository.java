package br.com.compesa.biochat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    Optional<Thread> findTopByFuncionarioOrderByDataCriacaoDesc(Funcionario funcionario);
}
