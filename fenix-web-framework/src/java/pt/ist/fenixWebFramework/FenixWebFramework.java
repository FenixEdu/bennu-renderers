package pt.ist.fenixWebFramework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelRangeFilter;

import pt.ist.fenixWebFramework.repository.SQLUpdateGenerator;
import pt.ist.fenixWebFramework.servlets.filters.CASFilter;
import pt.ist.fenixframework.FenixFramework;
import pt.utl.ist.fenix.tools.file.DSpaceFileManagerFactory;
import pt.utl.ist.fenix.tools.file.FileManagerFactory;
import pt.utl.ist.fenix.tools.util.FileUtils;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import dml.DomainModel;

public class FenixWebFramework extends FenixFramework {

    private static final Object INIT_LOCK = new Object();

    public static void initialize(final Config config) {
	synchronized (INIT_LOCK) {
	    FenixFramework.initialize(config);

	    updateDataRepositoryStructure(config);

	    final Locale locale = new Locale(config.getDefaultLanguage(), config.getDefaultLocation(), config.getDefaultVariant());
	    Language.setDefaultLocale(locale);

	    initializeLoggingSystem(config);

	    initializeFileManager(config);

	    initializeCas(config);;
	}
    }

    private static void updateDataRepositoryStructure(final Config config) {
	if (config.updateDataRepositoryStructure) {
	    Connection connection = null;
	    try {
		connection = getConnection(config);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
		    statement = connection.createStatement();
		    resultSet = statement.executeQuery("SELECT GET_LOCK('FenixFrameworkInit', 100)");
		    if (!resultSet.next() || (resultSet.getInt(1) != 1)) {
			return;
		    }
		} finally {
		    if (resultSet != null) {
			resultSet.close();
		    }
		    if (statement != null) {
			statement.close();
		    }
		}

		try {
		    createInfraestructure(connection, config);
		    final String updates = SQLUpdateGenerator.generateInMem(connection);
		    executeSqlInstructions(connection, updates);		    
		} finally {
		    Statement statementUnlock = null;
		    try {
			statementUnlock = connection.createStatement();
			statementUnlock.executeUpdate("DO RELEASE_LOCK('FenixFrameworkInit')");
		    } finally {
			if (statementUnlock != null) {
			    statementUnlock.close();
			}
		    }
		}

		connection.commit();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    } finally {
		if (connection != null) {
		    try {
			connection.close();
		    } catch (SQLException e) {
			// nothing can be done.
		    }
		}
	    }
	}
    }

    private static boolean infraestructureExists(final Connection connection, final Config config) throws SQLException {
	final DatabaseMetaData databaseMetaData = connection.getMetaData();
	ResultSet resultSet = null;
	try {
	    final String dbName = connection.getCatalog();
	    resultSet = databaseMetaData.getTables(dbName, "", "TX_CHANGE_LOGS", new String[] {"TABLE"});
	    
	    while (resultSet.next()) {
		final String tableName = resultSet.getString(3);
		if (tableName.equals("TX_CHANGE_LOGS")) {
		    return true;
		}
	    }
	    return false;
	} finally {
	    if (resultSet != null) {
		resultSet.close();
	    }
	}
    }

    private static void executeSqlInstructions(final Connection connection, final String sqlInstructions) throws IOException, SQLException {
	for (final String instruction : sqlInstructions.split(";")) {
	    final String trimmed = instruction.trim();
	    if (trimmed.length() > 0) {
		Statement statement = null;
		try {
		    statement = connection.createStatement();
		    statement.execute(instruction);
		} finally {
		    if (statement != null) {
			statement.close();
		    }
		}
	    }
	}
    }

    private static void executeSqlStream(final Connection connection, final String streamName) throws IOException, SQLException {
	final InputStream inputStream = FenixWebFramework.class.getResourceAsStream(streamName);
	final String sqlInstructions = FileUtils.readFile(inputStream);
	executeSqlInstructions(connection, sqlInstructions);
    }

    public static Connection getConnection(final Config config) throws ClassNotFoundException, SQLException {
	final String driverName = "com.mysql.jdbc.Driver";
	Class.forName(driverName);
	final String url = "jdbc:mysql:" + config.getDbAlias();
	final Connection connection = DriverManager.getConnection(url, config.getDbUsername(), config.getDbPassword());
	connection.setAutoCommit(false);
	return connection;
    }

    private static void createInfraestructure(final Connection connection, final Config config) throws SQLException, IOException {
	if (!infraestructureExists(connection, config)) {
	    executeSqlStream(connection, "/pt/ist/fenixWebFramework/dml.sql");
	    executeSqlStream(connection, "/pt/ist/fenixWebFramework/ojb.sql");
	}
    }

    private static void initializeLoggingSystem(final Config config) {
	if (config.logProfileFilename != null) {
	    final Logger logger = Logger.getLogger("pt.ist.fenixWebFramework.servlets.filters.ProfilingFilter");
	    logger.setAdditivity(false);

	    final Layout layout = new PatternLayout("%d{HH:mm:ss.SSS} %m%n");
	    final String filename = config.logProfileDir == null ? config.logProfileFilename : config.logProfileDir
		    + File.separatorChar + config.logProfileFilename;
	    try {
		final FileAppender fileAppender = new FileAppender(layout, filename, true);
		fileAppender.setName("com.atlassian.util.profiling");
		fileAppender.setThreshold(Priority.INFO);

		final LevelRangeFilter levelRangeFilter = new LevelRangeFilter();
		levelRangeFilter.setLevelMin(Level.INFO);
		levelRangeFilter.setLevelMax(Level.WARN);
		levelRangeFilter.setAcceptOnMatch(true);
		fileAppender.addFilter(levelRangeFilter);
		final DenyAllFilter denyAllFilter = new DenyAllFilter();
		fileAppender.addFilter(denyAllFilter);

		logger.addAppender(fileAppender);
	    } catch (IOException ex) {
		//throw new Error(ex);
		final Logger l = Logger.getLogger(FenixWebFramework.class);
		logger.warn("Profile logger not initialized correctly.");
	    }
	}
    }

    private static void initializeFileManager(Config config) {
	if (config.getDspaceClientTransportClass() != null) {
	    final Properties properties = new Properties();
	    properties.put("dspace.client.transport.class", config.getDspaceClientTransportClass());
	    properties.put("file.manager.factory.implementation.class", config.getFileManagerFactoryImplementationClass());
	    properties.put("dspace.serverUrl", config.getDspaceServerUrl());
	    properties.put("dspace.downloadUriFormat", config.getDspaceDownloadUriFormat());
	    properties.put("dspace.username", config.getDspaceUsername());
	    properties.put("dspace.password", config.getDspacePassword());
	    properties.put("dspace.rmi.server.name", config.getDspaceRmiServerName());
	    properties.put("jndi.properties.file", config.getJndiPropertiesFile());
	    properties.put("rmi.registry.port", config.getRmiRegistryPort());
	    properties.put("rmi.port", config.getRmiPort());
	    properties.put("rmi.ssl", config.getRmiSsl());
	    properties.put("rmi.ssl.truststore", config.getRmiSslTruststore());
	    properties.put("rmi.ssl.truststore.password", config.getRmiSslTruststorePassword());
	    properties.put("rmi.stream.bytes.min", config.getRmiStreamBytesMin());
	    properties.put("rmi.stream.bytes.max", config.getRmiStreamBytesMax());
	    properties.put("rmi.stream.bytes.block", config.getRmiStreamBytesBlock());

	    FileManagerFactory.init(config.getFileManagerFactoryImplementationClass());
	    
	    DSpaceFileManagerFactory.init(properties);
	}
    }

    private static void initializeCas(Config config) {
	CASFilter.init(config);
    }

    public static Config getConfig() {
	return (Config) FenixFramework.getConfig();
    }

    public static DomainModel getDomainModel() {
	return FenixFramework.getDomainModel();
    }

}
