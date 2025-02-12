package br.com.compesa.redmine;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RedmineService {

    private final RedmineManager redmineManager;

    public RedmineService(
            @Value("${redmine.url}") String redmineUrl,
            @Value("${redmine.api.key}") String apiAccessKey) {
        this.redmineManager = RedmineManagerFactory.createWithApiKey(redmineUrl, apiAccessKey);
    }

   // 🔹 Método para buscar todos os projetos
    public List<Project> getProjects() throws RedmineException {
        return redmineManager.getProjectManager().getProjects();
    }

    // 🔹 Buscar todas as issues de um projeto
    public List<Issue> getIssues(String projectKey) throws RedmineException {
        IssueManager issueManager = redmineManager.getIssueManager();
        return issueManager.getIssues(projectKey, null);
    }


}
