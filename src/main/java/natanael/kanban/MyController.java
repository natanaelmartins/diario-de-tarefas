package natanael.kanban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import natanael.kanban.model.CadastroEntity;
import natanael.kanban.model.TarefasEntity;
import natanael.kanban.repositories.CadastroRepository;

@Controller
public class MyController {
    
    @Autowired
    CadastroRepository cadastroRepository;

    CadastroEntity login = new CadastroEntity();

    TarefasEntity tarefas = new TarefasEntity();

    boolean telaCadastro = false;

    @GetMapping("/")
    public String login(ModelMap model) {

        telaCadastro = false;

        model.addAttribute("telaCadastro", false);

        return "login";
    }

    @PostMapping("/home")
    public String home(ModelMap model, String email, String password, String cadastro) {

        if ((!telaCadastro) && ("usuarioDesejaCadastro".equals(cadastro))) {
            return "redirect:/cadastro";
        }

        model.addAttribute("tarefa", tarefas);

        if ("admin@admin".equals(email)) {
            return "home";
        }

        return "redirect:/";
    }

    @GetMapping("/cadastro")
    public String cadastro(ModelMap model, String username, String password) {

        model.addAttribute("telaCadastro", true);

        telaCadastro = true;

        return "login";
    }
}