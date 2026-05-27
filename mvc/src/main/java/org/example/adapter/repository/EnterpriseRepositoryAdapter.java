package org.example.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.example.adapter.repository.mapper.EnterpriseMapper;
import org.example.model.Enterprise;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterpriseRepositoryAdapter implements org.example.application.repository.EnterpriseRepository {
    private final org.example.adapter.repository.jpa.EnterpriseRepository enterpriseRepository;
    private final EnterpriseMapper enterpriseMapper;

    @Override
    public List<Enterprise> findAllByManagerId(UUID managerId) {
        return enterpriseMapper.toModel(enterpriseRepository.findAllByManagerEntities_Id(managerId));
    }

    @Override
    public Optional<Enterprise> findByIdAndManagerId(UUID id, UUID managerId) {
        return enterpriseRepository.findByIdAndManagerEntities_Id(id, managerId)
                .map(enterpriseMapper::toModel);
    }

    @Override
    public Optional<Enterprise> findById(UUID id) {
        return enterpriseRepository.findById(id)
                .map(enterpriseMapper::toModel);
    }

    @Override
    public Enterprise save(Enterprise enterprise) {
        EnterpriseEntity enterpriseEntity = getEntityForSave(enterprise);
        EnterpriseEntity savedEnterpriseEntity = enterpriseRepository.save(enterpriseEntity);

        return enterpriseMapper.toModel(savedEnterpriseEntity);
    }

    @Override
    public void deleteById(UUID id) {
        enterpriseRepository.deleteById(id);
    }

    private EnterpriseEntity getEntityForSave(Enterprise enterprise) {
        Optional<EnterpriseEntity> existingEnterprise = Optional.empty();
        if (enterprise.getId() != null) {
            existingEnterprise = enterpriseRepository.findById(enterprise.getId());
        }

        if (existingEnterprise.isEmpty()) {
            return enterpriseMapper.toEntity(enterprise);
        }

        EnterpriseEntity enterpriseEntity = existingEnterprise.get();
        enterpriseMapper.updateEntity(enterprise, enterpriseEntity);

        return enterpriseEntity;
    }
}
