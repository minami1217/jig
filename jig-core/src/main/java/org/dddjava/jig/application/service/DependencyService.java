package org.dddjava.jig.application.service;

import org.dddjava.jig.domain.model.characteristic.Characteristic;
import org.dddjava.jig.domain.model.characteristic.CharacteristicRepository;
import org.dddjava.jig.domain.model.identifier.namespace.PackageDepth;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifier;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifiers;
import org.dddjava.jig.domain.model.relation.dependency.DependencyRepository;
import org.dddjava.jig.domain.model.relation.dependency.PackageDependencies;
import org.dddjava.jig.domain.model.characteristic.Characteristic;
import org.dddjava.jig.domain.model.characteristic.CharacteristicRepository;
import org.dddjava.jig.domain.model.identifier.namespace.PackageDepth;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifier;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifiers;
import org.dddjava.jig.domain.model.relation.dependency.DependencyRepository;
import org.dddjava.jig.domain.model.relation.dependency.PackageDependencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DependencyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyService.class);

    private final CharacteristicRepository characteristicRepository;
    private final DependencyRepository dependencyRepository;

    public DependencyService(CharacteristicRepository characteristicRepository, DependencyRepository dependencyRepository) {
        this.characteristicRepository = characteristicRepository;
        this.dependencyRepository = dependencyRepository;
    }

    public PackageDependencies packageDependencies(PackageDepth packageDepth) {
        LOGGER.info("パッケージ依存情報を取得します(深度: {})", packageDepth.value());
        TypeIdentifiers modelTypes = characteristicRepository.getTypeIdentifiersOf(Characteristic.MODEL);

        PackageDependencies packageDependencies = dependencyRepository
                .findAllTypeDependency()
                .toPackageDependenciesWith(modelTypes);

        showDepth(packageDependencies);

        return packageDependencies.applyDepth(packageDepth);
    }

    public void registerDependency(TypeIdentifier typeIdentifier, TypeIdentifiers typeIdentifiers) {
        dependencyRepository.registerDependency(typeIdentifier, typeIdentifiers);
    }

    private void showDepth(PackageDependencies packageDependencies) {
        PackageDepth maxDepth = packageDependencies.allPackages().maxDepth();

        LOGGER.info("最大深度: {}", maxDepth.value());
        for (PackageDepth depth : maxDepth.surfaceList()) {
            PackageDependencies dependencies = packageDependencies.applyDepth(depth);
            LOGGER.info("深度 {} の関連数: {} ", depth.value(), dependencies.number().asText());
        }
    }
}