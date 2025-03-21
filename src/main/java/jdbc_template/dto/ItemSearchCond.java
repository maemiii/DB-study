package jdbc_template.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ItemSearchCond {
    private String name;
    private Integer maxPrice;
}