package jdbc_template.v1;


import jdbc_template.dto.Item;
import jdbc_template.dto.ItemCreateDto;
import jdbc_template.dto.ItemSearchCond;
import jdbc_template.dto.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class JdbcTemplateV1Example {
    public static void main(String[] args) {
        JdbcTemplateRepositoryV1 repository = new JdbcTemplateRepositoryV1();

//    Item createdItem = repository.save(new ItemCreateDto("감자", 3000, 3));
//    Item createdItem = repository.save(new ItemCreateDto("곶감", 5000, 5));
//    Item createdItem = repository.save(new ItemCreateDto("사과", 4000, 4));

//        log.info("createdItem: {}", createdItem);

//        ItemUpdateDto updateParam = new ItemUpdateDto("바나나", 9000, 10);
//        if (repository.update(3L, updateParam)) {
//            log.info("update complete");
//        } else {
//            log.info("update failed");
//        }

        Long requestFindId = 3L;
        Optional<Item> foundItem = repository.findById(requestFindId);
        foundItem.ifPresentOrElse(
                it -> log.info("found Item: {}", it),
                () -> log.info("Not found Item by id: {}", requestFindId)
        );

        ItemSearchCond searchCondition = new ItemSearchCond();
         searchCondition.setName("김");
         searchCondition.setMaxPrice(10000);
        List<Item> items = repository.findAll(searchCondition);
        log.info("item count: {}", items.size());
        for (Item item : items) {
            log.info("item: {}", item);
        }
    }
}
