import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class EmployeeCreateRequest {
    private String name;
    private Integer age;
    private String position;
    private Integer salary;
    private Department department;
}
