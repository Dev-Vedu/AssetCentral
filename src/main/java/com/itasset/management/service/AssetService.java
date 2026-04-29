package com.itasset.management.service;

import com.itasset.management.model.Asset;
import com.itasset.management.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public Asset save(Asset asset) {
        return assetRepository.save(asset);
    }

    public List<Asset> getAssetsByUser(String userId) {
        return assetRepository.findByAssignedTo(userId);
    }

    public Asset getAssetById(Long id) {
        return assetRepository.findById(id).orElse(null);
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
    public Asset getById(Long id) {
        return assetRepository.findById(id).orElse(null);
    }
    public void delete(Long id) {
        assetRepository.deleteById(id);
    }

    public Asset update(Asset asset) {
        return assetRepository.save(asset);
    }


}
