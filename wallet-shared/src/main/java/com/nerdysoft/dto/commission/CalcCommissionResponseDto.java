package com.nerdysoft.dto.commission;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalcCommissionResponseDto {
    private BigDecimal commissionAmount;
}
