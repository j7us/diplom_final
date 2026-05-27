package org.example.adapter.repository.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.AuthGrantedAuthority;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.example.adapter.repository.entity.ManagerEntity;
import org.example.model.Manager;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring")
public interface ManagerMapper {
    @Mapping(target = "auth", source = "auth", qualifiedByName = "toAuthorityNames")
    @Mapping(target = "enterpriseIds", source = "enterprises", qualifiedByName = "toEnterpriseIds")
    Manager toModel(ManagerEntity managerEntity);

    @Mapping(target = "auth", source = "auth", qualifiedByName = "toAuthorityEntities")
    @Mapping(target = "enterprises", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    ManagerEntity toEntity(Manager manager);

    @Named("toAuthorityNames")
    default List<String> toAuthorityNames(List<AuthGrantedAuthority> auth) {
        if (CollectionUtils.isEmpty(auth)) {
            return new ArrayList<>();
        }

        return auth.stream()
                .map(AuthGrantedAuthority::getAuthority)
                .toList();
    }

    @Named("toEnterpriseIds")
    default List<UUID> toEnterpriseIds(List<EnterpriseEntity> enterprises) {
        if (CollectionUtils.isEmpty(enterprises)) {
            return new ArrayList<>();
        }

        return enterprises.stream()
                .map(EnterpriseEntity::getId)
                .toList();
    }

    @Named("toAuthorityEntities")
    default List<AuthGrantedAuthority> toAuthorityEntities(List<String> auth) {
        if (CollectionUtils.isEmpty(auth)) {
            return new ArrayList<>();
        }

        return auth.stream()
                .map(this::toAuthorityEntity)
                .toList();
    }

    default AuthGrantedAuthority toAuthorityEntity(String authority) {
        AuthGrantedAuthority authGrantedAuthority = new AuthGrantedAuthority();
        authGrantedAuthority.setId(UUID.randomUUID());
        authGrantedAuthority.setAuthority(authority);

        return authGrantedAuthority;
    }

    @AfterMapping
    default void linkAuth(@MappingTarget ManagerEntity managerEntity) {
        if (CollectionUtils.isEmpty(managerEntity.getAuth())) {
            return;
        }

        managerEntity.getAuth()
                .forEach(authGrantedAuthority -> authGrantedAuthority.setManagerEntity(managerEntity));
    }
}
