package com.nerdysoft.repo;

import com.nerdysoft.entity.ExchangeRate;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {
    Optional<ExchangeRate> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}
