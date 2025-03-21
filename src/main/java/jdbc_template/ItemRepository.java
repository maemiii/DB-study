package jdbc_template;

import jdbc_template.dto.Item;
import jdbc_template.dto.ItemCreateDto;
import jdbc_template.dto.ItemSearchCond;
import jdbc_template.dto.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(ItemCreateDto item);

    boolean update(Long itemId, ItemUpdateDto updateParam);

    Optional<Item> findById(Long itemId);

    List<Item> findAll(ItemSearchCond itemSearch);
}