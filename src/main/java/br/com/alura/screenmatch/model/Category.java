package br.com.alura.screenmatch.model;

public enum Category {
    ACTION("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDY("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIMINAL("Crime", "Crime"),
    ADVENTURE("Adventure", "Aventura");

    private String categoryToOmdb;
    private String categoryTranslate;

    Category(String categoryToOmdb, String categoryTranslate) {
        this.categoryToOmdb = categoryToOmdb;
        this.categoryTranslate = categoryTranslate;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.categoryToOmdb.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Category fromTranslate(String text) {
        for (Category category : Category.values()) {
            if (category.categoryTranslate.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
