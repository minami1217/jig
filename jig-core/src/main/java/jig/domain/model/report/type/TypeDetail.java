package jig.domain.model.report.type;

import jig.domain.model.characteristic.Characteristic;
import jig.domain.model.japanasename.JapaneseName;
import jig.domain.model.japanasename.JapaneseNameRepository;
import jig.domain.model.relation.Relation;
import jig.domain.model.relation.RelationRepository;
import jig.domain.model.relation.RelationType;
import jig.domain.model.relation.Relations;
import jig.domain.model.thing.Name;
import jig.domain.model.thing.Names;

public class TypeDetail {

    private final Name name;
    private final Characteristic characteristic;
    private RelationRepository relationRepository;
    private JapaneseNameRepository japaneseNameRepository;

    public TypeDetail(Name name, Characteristic characteristic, RelationRepository relationRepository, JapaneseNameRepository japaneseNameRepository) {
        this.name = name;
        this.characteristic = characteristic;
        this.relationRepository = relationRepository;
        this.japaneseNameRepository = japaneseNameRepository;
    }

    public Name name() {
        return name;
    }

    public JapaneseName japaneseName() {
        return japaneseNameRepository.get(name());
    }

    public Names usage() {
        Relations fieldRelation = relationRepository.findTo(name(), RelationType.FIELD);

        // TODO メソッドでの使用をどのように扱うか
        // Relations methodReturnRelation = relationRepository.findTo(name(), RelationType.METHOD_RETURN_TYPE);
        // Relations methodParameterRelation = relationRepository.findTo(name(), RelationType.METHOD_PARAMETER);

        return fieldRelation.list().stream().map(Relation::from).collect(Names.collector());
    }

    public boolean isTag(Characteristic characteristic) {
        return this.characteristic.matches(characteristic);
    }
}