package com.nerdysoft.repo;

import com.nerdysoft.entity.ExchangeRate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ExchangeRateRepository extends MongoRepository<ExchangeRate, String> {
    Optional<ExchangeRate> findByBaseCode(String baseCode);

    @Query("{ 'baseCode': ?0, 'conversionRates.?1': { $exists: true }, "
            + "'timestamp': { $gte: ?2, $lt: ?3 } }")
    Optional<ExchangeRate> findByFromCurrencyAndToCurrency(
            String fromCurrency,
            String toCurrency,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);
}
