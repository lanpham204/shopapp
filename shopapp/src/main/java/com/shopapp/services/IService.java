package com.shopapp.services;



import com.shopapp.exception.DataNotFoundException;

import java.util.List;

public interface IService<EntityType,EntityDTO, KeyType> {
    EntityType create(EntityDTO entityDTO) throws DataNotFoundException;
    EntityType getById(KeyType id) throws DataNotFoundException;
    List<EntityType> getAll();
    EntityType update(KeyType id, EntityDTO entityDTO) throws DataNotFoundException;
    void delete(KeyType id) throws DataNotFoundException;
}
