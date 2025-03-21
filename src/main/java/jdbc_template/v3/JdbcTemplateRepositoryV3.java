package jdbc_template.v3;

import jdbc_template.ItemRepository;
import jdbc_template.dto.Item;
import jdbc_template.dto.ItemCreateDto;
import jdbc_template.dto.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;

@Slf4j
public class JdbcTemplateRepositoryV3 implements ItemRepository {
    private final NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource());
    private final SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource())
            .withTableName("item")
            .usingGeneratedKeyColumns("id");

    private DataSource dataSource() {
        final String DRIVE_CLASS = "com.mysql.cj.jdbc.Driver";
        final String URL = "jdbc:mysql://localhost:3306/rdb_study";
        final String USERNAME = "root";
        final String PASSWORD = "";
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVE_CLASS);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    public Item save(ItemCreateDto createParam) {

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(createParam);
        Number key = jdbcInsert.executeAndReturnKey(param);

        Item item = new Item();
        item.setId(key.longValue());
        item.setName(createParam.getName());
        item.setPrice(createParam.getPrice());
        item.setQuantity(createParam.getQuantity());

        return item;
    }

    public boolean update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "UPDATE item " +
                "SET name = :name, price = :price, quantity = :quantity WHERE id = :id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", updateParam.getName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);
        return template.update(sql, param) > 0;
    }


}
