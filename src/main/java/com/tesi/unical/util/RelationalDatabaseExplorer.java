package com.tesi.unical.util;

import java.util.*;

public class RelationalDatabaseExplorer {

    // Metodo ricorsivo per esplorare le relazioni tra le tabelle
    public static void exploreRelations(String tableName, Map<String, List<String>> databaseSchema, Set<String> visitedTables) {
        if (!visitedTables.contains(tableName)) {
            visitedTables.add(tableName);

            // Stampa le relazioni della tabella corrente
            System.out.println("Relazioni per la tabella " + tableName + ":");

            // Ottieni le relazioni della tabella corrente
            List<String> relations = databaseSchema.get(tableName);
            if (relations != null) {
                for (String relatedTable : relations) {
                    System.out.println(tableName + " -> " + relatedTable);
                    // Esplora ricorsivamente le relazioni della tabella correlata
                    exploreRelations(relatedTable, databaseSchema, visitedTables);
                }
            } else {
                System.out.println("Nessuna relazione trovata per la tabella " + tableName);
            }
        }
    }

    public static void main(String[] args) {
        // Database Schema: Mappa di tabelle con le loro relazioni
        Map<String, List<String>> databaseSchema = new HashMap<>();
        // Inserisci qui il tuo schema del database
        databaseSchema.put("employees", Arrays.asList("departments"));
        databaseSchema.put("departments", Arrays.asList("employees", "projects"));
        databaseSchema.put("projects", Arrays.asList("employees"));

        // Tabella radice da cui iniziare l'esplorazione
        String rootTable = "employees";

        // Inizializza un insieme per tenere traccia delle tabelle visitate
        Set<String> visitedTables = new HashSet<>();

        // Avvia l'esplorazione delle relazioni ricorsivamente
        exploreRelations(rootTable, databaseSchema, visitedTables);
    }
}