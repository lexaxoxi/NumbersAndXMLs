import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationMain {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationMain.class);

    public static void main(String[] args) {
        logger.info("Start");
        long start = System.currentTimeMillis();

        Properties prop = getProperties();
        Service service = buildService(prop);
        service.doAllTasks();

        long runningTime = (System.currentTimeMillis() - start) / 1000;

        logger.info("App  finished with time = {} seconds", runningTime);
    }

    private static Service buildService(Properties prop) {
        Service service = new Service();
        service.setDao(buildDao(prop));
        service.setXmlService(new ConvertToXML());
        service.setDirectory(prop.getProperty("directory"));
        service.setEntriesCount(Integer.parseInt(prop.getProperty("entriesCount")));
        service.setTableName(prop.getProperty("tableName"));
        service.setColumnName(prop.getProperty("columnName"));
        return service;
    }

    private static Dao buildDao(Properties prop) {
        Dao dao = new Dao();
        dao.setDbName(prop.getProperty("dbName"));
        dao.setHost(prop.getProperty("host"));
        dao.setPort(Integer.parseInt(prop.getProperty("port")));
        dao.setUser(prop.getProperty("user"));
        dao.setPass(prop.getProperty("password"));
        return dao;
    }

    private static Properties getProperties() {
        logger.info("Loading application properties");
        Properties prop = new Properties();
        try (InputStream inputStream = Service.class.getResourceAsStream("application.properties")) {
            prop.load(inputStream);
        } catch (IOException e) {
            logger.error("Properties loading failure", e);
            throw new IllegalStateException("Properties loading failure", e);
        }
        logger.info("Application properties successfully loaded");
        return prop;
    }
}