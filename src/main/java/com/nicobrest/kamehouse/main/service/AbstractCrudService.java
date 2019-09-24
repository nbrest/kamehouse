package com.nicobrest.kamehouse.main.service;

import com.nicobrest.kamehouse.main.dao.CrudDao;

public abstract class AbstractCrudService {

  protected <E,D> Long create(CrudDao<E> dao, D dto) {
    E entity = getModel(dto);
    validate(entity);
    return dao.create(entity);
  }
  
  protected abstract <E,D> E getModel(D dto);
  
  protected abstract <E> void validate(E entity); 
}