package jdbc_template.v2;

import jdbc_template.ItemRepository;
import jdbc_template.dto.Item;
import jdbc_template.dto.ItemCreateDto;
import jdbc_template.dto.ItemSearchCond;
import jdbc_template.dto.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JdbcTemplateRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource());

    private DataSource dataSource() {
        final String DRIVE_CLASS = "com.mysql.cj.jdbc.Driver";
        final String URL = "jdbc:mysql://localhost:3306/rdb_study";
        final String USERNAME = "root";
        final String PASSWORD = "password";
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVE_CLASS);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    public Item save(ItemCreateDto createParam) {
        String sql = "INSERT INTO item (name, price, quantity) " +
                "VALUES (:name, :price, :quantity)";

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(createParam);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        Long key = keyHolder.getKey().longValue();
        Item item = new Item();
        item.setId(key);
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

    @Override
    public Optional<Item> findById(Long itemId) {
        String sql = "SELECT * FROM item WHERE id = :id";
        try {
            Map<String, Long> param = Map.of("id", itemId);
            Item item = template.queryForObject(sql, param, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getName();
        Integer maxPrice = cond.getMaxPrice();

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "SELECT * FROM item";
        // 동적쿼리(Dynamic Query)
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " WHERE";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " name LIKE concat('%',:name,'%')";
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " AND";
            }
            sql += " price <= :maxPrice";
        }

        log.info("sql:{}", sql);
        return template.query(sql, param, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class);
    }
}