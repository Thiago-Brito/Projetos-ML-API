package br.com.compesa.biochat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BioPromptRepository extends JpaRepository<BioPrompt, Long> {
    List<BioPrompt> findByThreadOrderByDataHoraAsc(Thread thread);
}
