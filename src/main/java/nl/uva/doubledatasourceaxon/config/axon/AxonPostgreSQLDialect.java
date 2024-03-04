package nl.uva.doubledatasourceaxon.config.axon;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;

import java.sql.Types;

public class AxonPostgreSQLDialect extends PostgreSQLDialect {

    public AxonPostgreSQLDialect() {
        // This is to ensure the "drop-if-exists" feature is turned on. Version in reality should be higher.
        //TODO From Hibernate 6.2.x the minimum version is 10, so upgrading springboot will eventually fix this and a simple call to super() should do the job
        super(DatabaseVersion.make( 10 ) );
    }

    @Override
    protected String columnType(int sqlTypeCode) {
        if (SqlTypes.BLOB == sqlTypeCode) {
            return "BYTEA";
        }
        return super.columnType(sqlTypeCode);
    }

    @Override
    protected String castType(int sqlTypeCode) {
        if (SqlTypes.BLOB == sqlTypeCode) {
            return "BYTEA";
        }
        return super.castType(sqlTypeCode);
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        JdbcTypeRegistry jdbcTypeRegistry = typeContributions.getTypeConfiguration().getJdbcTypeRegistry();
        jdbcTypeRegistry.addDescriptor(Types.BLOB, BinaryJdbcType.INSTANCE);
    }

}
