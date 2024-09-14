package br.com.alura.screenmatch.model;

public enum Category {
    ACTION("Action"),
    ROMANCE("Romance"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    CRIMINAL("Crime");

    private String categoryToOmdb;

    Category(String categoryToOmdb) {
        this.categoryToOmdb = categoryToOmdb;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.categoryToOmdb.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
