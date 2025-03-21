package jdbc_template.v1;

import jdbc_template.ItemRepository;
import jdbc_template.dto.Item;
import jdbc_template.dto.ItemCreateDto;
import jdbc_template.dto.ItemSearchCond;
import jdbc_template.dto.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class JdbcTemplateRepositoryV1 implements ItemRepository {

    private final JdbcTemplate template = new JdbcTemplate(dataSource());

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
        String sql = "INSERT INTO item (name, price, quantity) VALUES (?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            // Auto Increment Key
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, createParam.getName());
            ps.setInt(2, createParam.getPrice());
            ps.setInt(3, createParam.getQuantity());
            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();

        Item item = new Item();
        item.setId(key);
        item.setName(createParam.getName());
        item.setPrice(createParam.getPrice());
        item.setQuantity(createParam.getQuantity());

        return item;
    }

    public boolean update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "UPDATE item SET name = ?, price = ?, quantity = ? WHERE id = ?";
        // String sql = "UPDATE item SET name = ?, quantity = ?, price = ? WHERE id = ?";
        return template.update(sql,
                updateParam.getName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId
        ) > 0;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        String sql = "SELECT * FROM item WHERE id = ?";
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), itemId);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "SELECT * FROM item";
        // 동적쿼리(Dynamic Query)
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " WHERE";
        }
        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " name LIKE concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " AND";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql:{}", sql);
        return template.query(sql, itemRowMapper(), param.toArray());
    }

    private RowMapper<Item> itemRowMapper() {
        return (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        };
    }
}
