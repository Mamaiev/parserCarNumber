package ua.db;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.model.ChasingNumber;

import java.util.List;

@Repository
public interface ChasingNumberRepository extends CrudRepository<ChasingNumber, Long> {

    List<ChasingNumber> findByUserId(Long userId);

    @Query("select DISTINCT(cN.userId) from ChasingNumber cN")
    List<Long> findDistinctByUserId();

    @Query("select cN.number from ChasingNumber cN where cN.userId = ?1")
    List<String> findNumberByUserId(Long userId);
}
