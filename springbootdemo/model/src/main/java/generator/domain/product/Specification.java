package generator.domain.product;

import lombok.Data;

import java.io.Serializable;

@Data
public class Specification implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    public Specification(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Specification() {
    }
}
