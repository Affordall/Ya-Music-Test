package com.realjamapps.yamusicapp.database.sql.mapper;

public interface IBaseMapper<From, To> {
    To map(From from);
}
