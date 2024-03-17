package com.shopapp.repositories;

import com.shopapp.models.Role;
import com.shopapp.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
}
