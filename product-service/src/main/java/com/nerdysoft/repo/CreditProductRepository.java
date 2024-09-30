package com.nerdysoft.repo;

import com.nerdysoft.entity.CreditProduct;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditProductRepository extends JpaRepository<CreditProduct, UUID> {
}
