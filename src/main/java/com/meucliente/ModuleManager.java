package com.meucliente.module;

import com.meucliente.module.Module.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class ModuleManager {

    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() { }

    public static void init() {
        MODULES.clear();
        // Nomes da especificacao. A logica de cada um entra no bloco 3;
        // por enquanto so ligam/desligam o estado.
        add("Aimbot", Category.COMBAT);
        add("AutoAttack", Category.COMBAT);
        add("ESP", Category.VISUALS);
        add("NameTags", Category.VISUALS);
        add("Tracers", Category.VISUALS);
        add("Fullbright", Category.VISUALS);
        add("ToggleSprint", Category.MOVEMENT);
        add("PlayerInfo", Category.PLAYER);
        add("FPS", Category.HUD);
        add("Coordenadas", Category.HUD);
        add("ModulosAtivos", Category.HUD);
        Collections.sort(MODULES, new Comparator<Module>() {
            @Override
            public int compare(Module a, Module b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    private static void add(String name, Category category) {
        MODULES.add(new Module(name, category));
    }

    public static List<Module> all() {
        return Collections.unmodifiableList(MODULES);
    }

    /** Filtra pelo nome, ignorando maiusculas e a coluna. */
    public static List<Module> search(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Module> result = new ArrayList<>();
        for (Module m : MODULES) {
            if (q.isEmpty() || m.getName().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(m);
            }
        }
        return result;
    }
}
