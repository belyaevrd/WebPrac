package ru.webprac.classes;

public enum UserRole {
    Teacher,
    Student;

    public String getRoleStr() {
        return switch (this) {
            case Teacher -> "Преподаватель";
            case Student -> "Обучающийся";
        };
    }
}
