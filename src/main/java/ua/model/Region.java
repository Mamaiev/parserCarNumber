package ua.model;

public enum Region {

    Crime(1, "АР Крим"),
    Vin(2, "Вінницька"),
    Vol(3, "Волинська"),
    Dni(4, "Дніпропетровська"),
    Don(5, "Донецька"),
    Zhy(6, "Житомирська"),
    Zak(7, "Закарпатська"),
    Zap(8, "Запорізька"),
    Iva(9, "Івано-Франківська"),
    Kyi(26, "м. Київ"),
    KyO(10, "Київська"),
    Kir(11, "Кіровоградська"),
    Lug(12, "Луганська"),
    Lvi(13, "Львівська"),
    Myk(14, "Миколаївська"),
    Ode(15, "Одеська"),
    Pol(16, "Полтавська"),
    Riv(17, "Рівненська"),
    Sum(18, "Сумська"),
    Ter(19, "Тернопільська"),
    Kha(20, "Харківська"),
    Khe(21, "Херсонська"),
    Khm(22, "Хмельницька"),
    Che(23, "Черкаська"),
    Chr(24, "Чернівецька"),
    Chi(25, "Чернігівська");

    private final int code;
    private final String name;

    Region(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }
}
