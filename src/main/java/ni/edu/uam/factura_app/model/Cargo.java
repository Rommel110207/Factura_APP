package ni.edu.uam.factura_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cargo {

    private Integer id;

    private String nombre;

    private String descripcion;


}
