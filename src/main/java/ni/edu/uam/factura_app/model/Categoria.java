package ni.edu.uam.factura_app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria
{
    private Integer id;

    private String nombre;

    private boolean activa;

    @Override

    public String toString() {
        return nombre;
    }


}
