package main.mappers;

import java.util.HashMap;

public class DefaultMapper {
    private HashMap<String, String> errors;

    public HashMap<String, String> getNotFoundError() {
        this.errors = new HashMap<>();
        this.errors.put("Результат поиска", "Ответ на запрос пустой!");
        return this.errors;
    }

    public HashMap<String, String> getEmptyJsonError() {
        this.errors = new HashMap<>();
        this.errors.put("Пустая json строка", "В ответ на запрос от Marvel API пришла пустая json строка!");
        return this.errors;
    }

    public HashMap<String, String> getParsingError() {
        this.errors = new HashMap<>();
        this.errors.put("Ошибка парсинга", "Произошла ошибка парсинга json строки!");
        return this.errors;
    }
}
