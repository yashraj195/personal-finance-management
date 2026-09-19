package com.yash.finance.repository;

import com.yash.finance.entity.Category;

import java.math.BigDecimal;

public interface CategorySpendingProjection {

    Category getCategory();

    BigDecimal getAmount();
}