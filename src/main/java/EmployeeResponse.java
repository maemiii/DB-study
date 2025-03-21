import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class EmployeeResponse {
    private long id;
    private String name;
    private String position;
    private int salary;
}
