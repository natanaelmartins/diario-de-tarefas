package natanael.kanban.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import natanael.kanban.model.UsuariosEntity;
import natanael.kanban.enums.ConstantesIA;
import natanael.kanban.enums.StatusTarefa;
import natanael.kanban.model.MetasEntity;
import natanael.kanban.model.TarefasEntity;
import natanael.kanban.repositories.MetasRepository;
import natanael.kanban.repositories.TarefasRepository;
import natanael.kanban.repositories.UsuariosRepository;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TarefasController {

    private ChatClient chatClient;

    @Autowired
    public void ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

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
    public String editarMeta(String id, String tituloMeta, Principal principal) {

        MetasEntity metaEditada = metasRepository.findById(UUID.fromString(id)).get();

        metaEditada.setTituloMeta(tituloMeta);

        metasRepository.save(metaEditada);

        return "redirect:/home/" + principal.getName();
    }

    @PostMapping("/deletarMeta")
    public String deletarMeta(String id) {

        List<TarefasEntity> listaTarefas = tarefasRepository.findByMetaId(UUID.fromString(id));
        
        tarefasRepository.deleteAll(listaTarefas);

        metasRepository.delete(metasRepository.findById(UUID.fromString(id)).get());

        return "redirect:/home";
    }

    @PostMapping("/adicionarTarefa")
    public String adicionarTarefa(String tarefa, String metaId, Principal principal) {

        tarefasRepository.save(new TarefasEntity(UUID.randomUUID(), tarefa, null,
                metasRepository.findById(UUID.fromString(metaId)).get(), StatusTarefa.LISTA_TAREFAS.getDescricao()));

        return "redirect:/home/tarefas/" + principal.getName() + "/" + metaId;
    }

    @PostMapping("/editarTarefa")
    public String editarTarefa(String id, String txt, Principal principal, String metaId) {

        TarefasEntity tarefaEditada = tarefasRepository.findById(UUID.fromString(id)).get();

        tarefaEditada.setTaskText(txt);

        tarefasRepository.save(tarefaEditada);

        return "redirect:/home/tarefas/" + principal.getName() + "/" + metaId;
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

    @PostMapping("/gerarTarefa")
    public String gerarTarefas(String objetivo, String metaId, Principal principal) {

        String resposta = chatClient.prompt().user(ConstantesIA.PROMPT.promptFormatado(objetivo)).call().content();

        Map<String, String> respostaMapeada = Map.of(ConstantesIA.GENERATION.getDescricao(), resposta);

        String respostaJson = respostaMapeada.get(ConstantesIA.GENERATION.getDescricao());

        JSONObject objetoJson = new JSONObject(respostaJson);

        JSONArray listaTarefas = objetoJson.getJSONArray(ConstantesIA.STEPS.getDescricao());

        List<String[]> listaTarefasFormatada = new ArrayList<>();

        for (int i = 0; i < listaTarefas.length(); i++) {
            JSONObject elemento = listaTarefas.getJSONObject(i);
            String titulo = elemento.getString("title");
            String descricao = elemento.getString("description");

            String[] tarefaFormatada = new String[] { titulo, descricao };

            listaTarefasFormatada.add(tarefaFormatada);
        }

        for (String[] tarefa : listaTarefasFormatada) {

            tarefasRepository.save(new TarefasEntity(UUID.randomUUID(), tarefa[0], tarefa[1],
                metasRepository.findById(UUID.fromString(metaId)).get(), StatusTarefa.LISTA_TAREFAS.getDescricao()));
        }

        return "redirect:/home/tarefas/" + principal.getName() + "/" + metaId;
    }
}