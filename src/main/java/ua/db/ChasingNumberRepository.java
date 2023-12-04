package ua.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.model.ChasingNumber;

import java.util.List;

@Repository
public interface ChasingNumberRepository extends CrudRepository<ChasingNumber, Long> {

    List<ChasingNumber> findByUserId(Long userId);
}
