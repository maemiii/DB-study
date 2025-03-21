package jdbc_template.v2;

import jdbc_template.dto.Item;
import jdbc_template.dto.ItemCreateDto;
import jdbc_template.dto.ItemSearchCond;
import jdbc_template.dto.ItemUpdateDto;
import jdbc_template.v1.JdbcTemplateRepositoryV1;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class JdbcTemplateV2Example {

    public static void main(String[] args) {
        JdbcTemplateRepositoryV2 repository = new JdbcTemplateRepositoryV2();

        // Item createdItem = repository.save(new ItemCreateDto("아이폰", 10000, 8));
        // log.info("createdItem: {}", createdItem);

        ItemUpdateDto updateParam = new ItemUpdateDto("갤럭시", 9000, 10);
        // ItemUpdateDto updateParam = new ItemUpdateDto("갤럭시", 10, 9000);
        if (repository.update(7L, updateParam)) {
            log.info("update complete");
        } else {
            log.info("update failed");
        }
        //
        // Long requestFindId = 100L;
        // Optional<Item> foundItem = repository.findById(requestFindId);
        // foundItem.ifPresentOrElse(
        //     it -> log.info("found Item: {}", it),
        //     () -> log.info("Not found Item by id: {}", requestFindId)
        // );

        ItemSearchCond searchCondition = new ItemSearchCond();
        // // searchCondition.setName("감");
        // // searchCondition.setMaxPrice(6000);
        List<Item> items = repository.findAll(searchCondition);
        log.info("item count: {}", items.size());
        for (Item item : items) {
            log.info("item: {}", item);
        }
    }
}