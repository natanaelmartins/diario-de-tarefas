package natanael.kanban.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import natanael.kanban.model.UsuariosEntity;
import natanael.kanban.model.MetasEntity;
import natanael.kanban.model.TarefasEntity;
import natanael.kanban.repositories.MetasRepository;
import natanael.kanban.repositories.TarefasRepository;
import natanael.kanban.repositories.UsuariosRepository;

@Controller
public class TarefasController {
    
    @Autowired
    UsuariosRepository usuariosRepository;

    @Autowired
    TarefasRepository tarefasRepository;

    @Autowired
    MetasRepository metasRepository;
    
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @GetMapping("/login")
	public String login() {

        return "login";
	}
    
    @GetMapping("/cadastro")
    public String cadastro() {

        return "cadastro";
    }

    @GetMapping("/editarPerfil")
    public String editarPerfil() {

        return "editarPerfil";
    }

    @GetMapping("/home")
    public String home(ModelMap model) {

        List<MetasEntity> listaMetas = metasRepository.findAll();

        model.addAttribute("listaMetas", listaMetas);

        return "home";
    }

    @GetMapping("/home/tarefas/{metaId}")
    public String tarefas(@PathVariable String metaId, ModelMap model) {

        Optional<MetasEntity> meta = metasRepository.findById(UUID.fromString(metaId));

        if (meta.isPresent()) {

            List<TarefasEntity> listaTarefas = tarefasRepository.findByMetaId(meta.get().getId());

            model.addAttribute("listaTarefas", listaTarefas);
            model.addAttribute("meta", meta.get());

        } else {
            return "redirec:/home";
        }
        
        return "tarefas";
    }


    @PostMapping("/salvarCadastro")
    public String salvarCadastro(String username, String email, String password) {

        UsuariosEntity usuario = new UsuariosEntity();

        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder().encode(password));

        try {
            usuariosRepository.save(usuario);
        } catch (Exception e) {
            System.out.println("Usuário já cadastrado!");
        }
        
        return "/login";
    }

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

    @PostMapping("/adicionarMeta")
    public String adicionarMeta(String meta) {

        metasRepository.save(new MetasEntity(UUID.randomUUID(), meta));

        return "redirect:/home";
    }

    @PostMapping("/editarMeta")
    public String editarMeta(String id, String tituloMeta) {
    
        MetasEntity metaEditada = metasRepository.findById(UUID.fromString(id)).get();

        metaEditada.setTituloMeta(tituloMeta);
    
        metasRepository.save(metaEditada);

        return "redirect:/home/tarefas";
    }

    @PostMapping("/deletarMeta")
    public String deletarMeta(String id) {

        metasRepository.delete(metasRepository.findById(UUID.fromString(id)).get());

        return "redirect:/home";
    }
    
    @PostMapping("/adicionarTarefa")
    public String adicionarTarefa(String tarefa, String metaId) {
                
        tarefasRepository.save(new TarefasEntity(UUID.randomUUID(), tarefa, metasRepository.findById(UUID.fromString(metaId)).get()));
        
        return "redirect:/home/tarefas";
    }

    @PostMapping("/editarTarefa")
    public String editarTarefa(String id, String txt) {
    
        TarefasEntity tarefaEditada = tarefasRepository.findById(UUID.fromString(id)).get();

        tarefaEditada.setTaskText(txt);
    
        tarefasRepository.save(tarefaEditada);

        return "redirect:/home/tarefas";
    }

    @PostMapping("/deletarTarefa")
    public String deletarTarefa(String id) {

        tarefasRepository.delete(tarefasRepository.findById(UUID.fromString(id)).get());

        return "redirect:/home/tarefas";
    }
}