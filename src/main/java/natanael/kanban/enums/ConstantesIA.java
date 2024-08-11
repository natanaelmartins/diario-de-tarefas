package natanael.kanban.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConstantesIA {

    PROMPT("Crie um roadmap para {objetivo} em formato JSON, contendo uma lista única chamda 'steps' com os campos 'title' e 'description'. No 'description' colocar links úteis. Por favor responda apenas com o JSON, começando com o '{'."),

    OBJETIVO("{objetivo}"),

    GENERATION("generation"),

    STEPS("steps");

    private String descricao;

    public String promptFormatado(String objetivo) {
        return descricao.replace(OBJETIVO.descricao, objetivo);
    }
}