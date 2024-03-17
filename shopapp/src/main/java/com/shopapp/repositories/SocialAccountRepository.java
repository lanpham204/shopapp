package com.shopapp.repositories;

import com.shopapp.models.Product;
import com.shopapp.models.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount,Long> {
}
