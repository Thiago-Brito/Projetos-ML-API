package br.com.compesa.redmine;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/redmine")
public class RedmineController {

    private final RedmineService redmineService;

    public RedmineController(RedmineService redmineService) {
        this.redmineService = redmineService;
    }

    // 🔹 Endpoint para obter todos os projetos do Redmine
    @GetMapping("/projects")
    public List<Project> getProjects() throws RedmineException {
        return redmineService.getProjects();
    }

     // 🔹 Buscar todas as issues do projeto
     @GetMapping("/issues/{projectKey}")
     public List<Issue> getIssues(@PathVariable String projectKey) throws RedmineException {
         return redmineService.getIssues(projectKey);
     }
}

