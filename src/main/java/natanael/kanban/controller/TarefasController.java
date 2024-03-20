package natanael.kanban.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import natanael.kanban.model.UsuariosEntity;
import natanael.kanban.model.TarefasEntity;
import natanael.kanban.repositories.TarefasRepository;
import natanael.kanban.repositories.UsuariosRepository;

@Controller
public class TarefasController {
    
    @Autowired
    UsuariosRepository usuariosRepository;

    @Autowired
    TarefasRepository tarefasRepository;

    UsuariosEntity usuarioLogado = new UsuariosEntity();
    
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    TarefasEntity tarefaCadastrada = new TarefasEntity();
    
    @GetMapping("/login")
	public String login() {

        return "login";
	}

    @GetMapping("/home")
    public String home(ModelMap model) {

        List<TarefasEntity> listaTarefas = tarefasRepository.findAll();

        model.addAttribute("listaTarefas", listaTarefas);

        return "home";
    }

    @GetMapping("/teste")
    public String teste() {

        return "teste";
    }

    @GetMapping("/cadastro")
    public String cadastro() {

        return "cadastro";
    }

    @GetMapping("/editarPerfil")
    public String editarPerfil() {

        return "editarPerfil";
    }
    
    @SuppressWarnings("null")
    @PostMapping("/salvarCadastro")
    public String salvarCadastro(String username, String email, String password) {

        usuarioLogado.setUsername(username);
        usuarioLogado.setEmail(email);
        usuarioLogado.setPassword(passwordEncoder().encode(password));

        try {
            usuariosRepository.save(usuarioLogado);
        } catch (Exception e) {
            System.out.println("Usuário já cadastrado!");
        }
        
        return "/login";
    }

    @SuppressWarnings("null")
    @PostMapping("/editarCadastro")
    public String editarCadastro(String username, String email, String password) {
        
        Optional<UsuariosEntity> usuarioAlterado = usuariosRepository.findByEmail(email);

        usuarioAlterado.get().setUsername(username);
        usuarioAlterado.get().setEmail(email);
        usuarioAlterado.get().setPassword(passwordEncoder().encode(password));

        try {
            usuariosRepository.save(usuarioAlterado.get());
        } catch (Exception e) {
            System.out.println("Alteração de dados não pôde ser concluída!");
        }
        
        return "redirect:/home";
    }

    @PostMapping("/adicionarTarefa")
    public String adicionarTarefa(String tarefa) {

        tarefaCadastrada.setTaskText(tarefa);

        tarefasRepository.save(tarefaCadastrada);

        System.out.println("testando");

        return "redirect:/home";
    }
}