package shardingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import shardingTest.lab.LabApplication;
import shardingTest.lab.mongodb.domain.MongoMessageEntity;
import shardingTest.lab.mysql.controller.model.MessageRequest;
import shardingTest.lab.mysql.domain.MySQLMessageEntity;
import shardingTest.lab.mysql.domain.MySQLMessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@SpringBootTest(classes = LabApplication.class)
public class DummyDataTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MySQLMessageRepository mySQLMessageRepository;

    @Test
    public void makeMySQLDummyData() {
        List<Long> roomIds = LongStream.rangeClosed(18001, 36500).boxed().toList(); // 두번 나눠서 저장(샤드 키를 controller에서 넣기 떄문)

        roomIds.parallelStream().forEach(roomId -> {
            List<MySQLMessageEntity> batch = new ArrayList<>();
            for(int senderId = 1; senderId <= 100; senderId++) {

            }
            for (int i = 0; i < 80; i++) {
                MessageRequest request = new MessageRequest("Hello", roomId, 101L, 102L, true);
                batch.add(MySQLMessageEntity.from(request));
            }

            for (int i = 0; i < 20; i++) {
                MessageRequest request = new MessageRequest("Hello", roomId, 101L, 102L, false);
                batch.add(MySQLMessageEntity.from(request));
            }

            mySQLMessageRepository.saveAll(batch);
        });
    }


    @Test
    public void makeMongoDummyData() {
        List<MongoMessageEntity> batch = new ArrayList<>();

        // 36500개의 채팅방에 총 100개의 메시지가 저장된다. (80개의 읽은 메시지 + 20개의 안읽은 메시지)
        for(Long roomId = 0L; roomId <= 36500; roomId++) {
            for(int i = 0; i < 80; i++) {
                MessageRequest request = new MessageRequest("Hello", roomId, 101L, 102L, true);
                MongoMessageEntity entity = MongoMessageEntity.from(request);
                batch.add(entity);
            }

            for(int i = 0; i < 20; i++) {
                MessageRequest request = new MessageRequest("Hello", roomId, 101L, 102L, true);
                MongoMessageEntity entity = MongoMessageEntity.from(request);
                batch.add(entity);
            }

            if (batch.size() >= 100) {
                mongoTemplate.insertAll(batch);
                batch.clear();
            }
        }
    }
}
