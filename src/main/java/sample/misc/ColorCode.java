package sample.misc;

public enum ColorCode {
    BOLD("BOLD",""),
    ITALIC("ITALIC",""),
    UNDERLINED("UNDERLINED",""),
    STRIKETHROUGH("STRIKETHROUGH",""),
    a("FILL","AQUA"),
    b("",""),
    c("FILL","CADETBLUE"),
    d("FILL","CYAN"),
    e("",""),
    f("",""),
    g("FILL","GOLDENROD"),
    h("FILL","GRAY"),
    i("",""),
    j("",""),
    k("",""),
    l("FILL","LIGHTGRAY"),
    m("",""),
    n("",""),
    o("",""),
    p("",""),
    q("",""),
    r("RESET",""),
    s("",""),
    t("",""),
    u("",""),
    v("",""),
    w("",""),
    x("FILL","WHITESMOKE"),
    y("",""),
    z("","");

    private final String type, style;

    ColorCode(String type, String style) {
        this.type = type;
        this.style = style;
    }

    public Entry<String, String> getEntry() {
        return new Entry<>(this.type, this.style);
    }
}
