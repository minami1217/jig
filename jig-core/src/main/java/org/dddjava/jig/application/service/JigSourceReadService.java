package org.dddjava.jig.application.service;

import org.dddjava.jig.application.repository.JigSourceRepository;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.alias.*;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.rdbaccess.Sqls;
import org.dddjava.jig.domain.model.jigsource.file.SourcePaths;
import org.dddjava.jig.domain.model.jigsource.file.SourceReader;
import org.dddjava.jig.domain.model.jigsource.file.Sources;
import org.dddjava.jig.domain.model.jigsource.file.binary.ClassSources;
import org.dddjava.jig.domain.model.jigsource.file.text.AliasSource;
import org.dddjava.jig.domain.model.jigsource.file.text.javacode.JavaSources;
import org.dddjava.jig.domain.model.jigsource.file.text.javacode.PackageInfoSources;
import org.dddjava.jig.domain.model.jigsource.file.text.kotlincode.KotlinSources;
import org.dddjava.jig.domain.model.jigsource.file.text.scalacode.ScalaSources;
import org.dddjava.jig.domain.model.jigsource.file.text.sqlcode.SqlSources;
import org.dddjava.jig.domain.model.jigsource.jigloader.FactReader;
import org.dddjava.jig.domain.model.jigsource.jigloader.SourceCodeAliasReader;
import org.dddjava.jig.domain.model.jigsource.jigloader.SqlReader;
import org.dddjava.jig.domain.model.jigsource.jigloader.analyzed.AnalyzedImplementation;
import org.dddjava.jig.domain.model.jigsource.jigloader.analyzed.TypeFacts;
import org.springframework.stereotype.Service;

/**
 * 取り込みサービス
 */
@Service
public class JigSourceReadService {

    final JigSourceRepository jigSourceRepository;
    final FactReader factReader;
    final SourceReader sourceReader;
    final SqlReader sqlReader;
    final SourceCodeAliasReader aliasReader;

    public JigSourceReadService(JigSourceRepository jigSourceRepository, FactReader factReader, SourceCodeAliasReader AliasReader, SqlReader sqlReader, SourceReader sourceReader) {
        this.jigSourceRepository = jigSourceRepository;
        this.factReader = factReader;
        this.aliasReader = AliasReader;
        this.sqlReader = sqlReader;
        this.sourceReader = sourceReader;
    }

    /**
     * パスからソースを読み取る
     */
    public AnalyzedImplementation readSourceFromPaths(SourcePaths sourcePaths) {
        Sources source = sourceReader.readSources(sourcePaths);

        TypeFacts typeFacts = readProjectData(source);
        Sqls sqls = readSqlSource(source.sqlSources());

        return new AnalyzedImplementation(source, typeFacts, sqls);
    }

    /**
     * プロジェクト情報を読み取る
     */
    public TypeFacts readProjectData(Sources sources) {
        TypeFacts typeFacts = readClassSource(sources.classSources());
        readAliases(sources.aliasSource());
        return typeFacts;
    }

    /**
     * ソースからバイトコードを読み取る
     */
    public TypeFacts readClassSource(ClassSources classSources) {
        TypeFacts typeFacts = factReader.readTypeFacts(classSources);
        jigSourceRepository.registerTypeFact(typeFacts);
        return typeFacts;
    }

    /**
     * ソースからSQLを読み取る
     */
    public Sqls readSqlSource(SqlSources sqlSources) {
        Sqls sqls = sqlReader.readFrom(sqlSources);
        jigSourceRepository.registerSqls(sqls);
        return sqls;
    }

    /**
     * Javadocからパッケージ別名を取り込む
     */
    void loadPackageInfoSources(PackageInfoSources packageInfoSources) {
        PackageAliases packageAliases = aliasReader.readPackages(packageInfoSources);
        for (PackageAlias packageAlias : packageAliases.list()) {
            jigSourceRepository.registerPackageAlias(packageAlias);
        }
    }

    /**
     * Javadocから別名を取り込む
     */
    void readJavaSources(JavaSources javaSources) {
        TypeAliases typeAliases = aliasReader.readJavaSources(javaSources);
        readTypeAlias(typeAliases);
    }

    /**
     * KtDocから別名を取り込む
     */
    void readKotlinSources(KotlinSources kotlinSources) {
        TypeAliases typeAliases = aliasReader.readKotlinSources(kotlinSources);
        readTypeAlias(typeAliases);
    }

    /**
     * ScalaDocから別名を取り込む
     */
    void readScalaSources(ScalaSources scalaSources) {
        TypeAliases typeAliases = aliasReader.readScalaSources(scalaSources);
        readTypeAlias(typeAliases);
    }

    /**
     * 型別名を取り込む
     */
    private void readTypeAlias(TypeAliases typeAliases) {
        for (TypeAlias typeAlias : typeAliases.list()) {
            jigSourceRepository.registerTypeAlias(typeAlias);
        }

        for (MethodAlias methodAlias : typeAliases.methodList()) {
            jigSourceRepository.registerMethodAlias(methodAlias);
        }
    }

    /**
     * 別名を取り込む
     */
    public void readAliases(AliasSource aliasSource) {
        readJavaSources(aliasSource.javaSources());
        readKotlinSources(aliasSource.kotlinSources());
        readScalaSources(aliasSource.scalaSources());
        loadPackageInfoSources(aliasSource.packageInfoSources());
    }
}
