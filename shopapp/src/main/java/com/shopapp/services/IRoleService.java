package com.shopapp.services;



import com.shopapp.exception.DataNotFoundException;
import com.shopapp.models.Role;

import java.util.List;

public interface IRoleService {
    List<Role> getAll();
}
