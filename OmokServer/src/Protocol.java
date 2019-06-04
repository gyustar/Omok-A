class Protocol {
    private String data;
    private String type;

    Protocol(String data, String type) {
        this.data = data;
        this.type = type;
    }

    String getData() {
        return data;
    }

    String getType() {
        return type;
    }
}