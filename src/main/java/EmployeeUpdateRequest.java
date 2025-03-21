import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class EmployeeUpdateRequest {
    private String position;
    private Integer salary;
}