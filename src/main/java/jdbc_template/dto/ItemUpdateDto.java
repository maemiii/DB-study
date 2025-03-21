package jdbc_template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemUpdateDto {
    private String name;
    private Integer price;
    private Integer quantity;
}