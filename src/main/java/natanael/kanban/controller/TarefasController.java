package natanael.kanban.controller;

import java.security.Principal;
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
import natanael.kanban.enums.statusTarefa;
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
    public String editarPerfil(ModelMap model, Principal principal) {

        model.addAttribute("username", principal.getName());

        return "editarPerfil";
    }

    @GetMapping("/home/{username}")
    public String home(@PathVariable String username, ModelMap model, Principal principal) {

        if (!principal.getName().equals(username)) {
            return "redirect:/logout";
        }

        List<MetasEntity> listaMetas = metasRepository.findByUsuarioUsername(username);

        model.addAttribute("listaMetas", listaMetas);
        model.addAttribute("username", username);

        return "home";
    }

    @GetMapping("/home/tarefas/{username}/{metaId}")
    public String tarefas(@PathVariable String metaId, @PathVariable String username, ModelMap model,
            Principal principal) {

        Optional<MetasEntity> meta = metasRepository.findById(UUID.fromString(metaId));

        if (!principal.getName().equals(username)
                || !meta.get().getUsuario().getUsername().equals(principal.getName())) {
            return "redirect:/logout";
        }

        List<TarefasEntity> listaTarefas = tarefasRepository.findByMetaId(meta.get().getId());

        model.addAttribute("listaTarefas", listaTarefas);
        model.addAttribute("meta", meta.get());
        model.addAttribute("username", username);

        return "tarefas";
    }

    @PostMapping("/salvarCadastro")
    public String salvarCadastro(String username, String email, String password) throws Exception {

        if (usuariosRepository.findByUsername(username).isPresent()) {
            return "/login";
            // mensagem de erro
        }

        if (usuariosRepository.findByEmail(email).isPresent()) {
            return "/login";
            // mensagem de erro
        }

        UsuariosEntity usuario = new UsuariosEntity();

        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder().encode(password));

        usuariosRepository.save(usuario);

        return "/login";
    }

    @PostMapping("/editarCadastro")
    public String editarCadastro(String username, String email, String password, Principal principal) {

        if (usuariosRepository.findByUsername(username).isPresent()) {
            return "/login";
            // mensagem de erro
        }

        if (usuariosRepository.findByEmail(email).isPresent()) {
            return "/login";
            // mensagem de erro
        }

        Optional<UsuariosEntity> usuarioAlterado = usuariosRepository.findByUsername(principal.getName());

        if (!username.isEmpty()) {
            usuarioAlterado.get().setUsername(username);
        }

        if (!email.isEmpty()) {
            usuarioAlterado.get().setEmail(email);
        }

        if (!password.isEmpty()) {
            usuarioAlterado.get().setPassword(passwordEncoder().encode(password));
        }

        usuariosRepository.save(usuarioAlterado.get());

        return "redirect:/home";
    }

    @PostMapping("/adicionarMeta")
    public String adicionarMeta(String meta, Principal principal) {

        metasRepository
                .save(new MetasEntity(UUID.randomUUID(), meta,
                        usuariosRepository.findByUsername(principal.getName()).get()));

        return "redirect:/home/" + principal.getName();
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
    public String adicionarTarefa(String tarefa, String metaId, Principal principal) {

        tarefasRepository.save(new TarefasEntity(UUID.randomUUID(), tarefa,
                metasRepository.findById(UUID.fromString(metaId)).get(), statusTarefa.LISTA_TAREFAS.getDescricao()));

        return "redirect:/home/tarefas/" + principal.getName() + "/" + metaId;
    }

    @PostMapping("/editarTarefa")
    public String editarTarefa(String id, String txt) {

        TarefasEntity tarefaEditada = tarefasRepository.findById(UUID.fromString(id)).get();

        tarefaEditada.setTaskText(txt);

        tarefasRepository.save(tarefaEditada);

        return "redirect:/home/tarefas";
    }

    @PostMapping("/editarStatusTarefa")
    public String editarStatusTarefa(String id, String status, String metaId, Principal principal) {

        TarefasEntity tarefa = tarefasRepository.findById(UUID.fromString(id)).get();

        tarefa.setStatus(status);

        tarefasRepository.save(tarefa);

        return "redirect:/home/tarefas/" + principal.getName() + "/" + metaId;
    }

    @PostMapping("/deletarTarefa")
    public String deletarTarefa(String id, String metaId, Principal principal) {

        tarefasRepository.delete(tarefasRepository.findById(UUID.fromString(id)).get());

        return "redirect:/home/tarefas/" + principal.getName() + "/" + metaId;
    }
}