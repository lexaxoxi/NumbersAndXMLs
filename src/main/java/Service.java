import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Service {
    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    private Dao dao;
    private ConvertToXML convertToXml;
    private String directory;
    private int entriesCount;
    private String tableName;
    private String columnName;

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void setXmlService(ConvertToXML convertToXml) {
        this.convertToXml = convertToXml;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setEntriesCount(int entriesCount) {
        this.entriesCount = entriesCount;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void printSum(List<Integer> values) {
        logger.info("Calculating the sum of numbers");
        logger.info("Sum of all numbers = {}", values.stream().mapToLong(i -> i).sum());
    }

    public void doAllTasks() {
        dao.inputDB(tableName, columnName, entriesCount);

        File source = new File(directory + "1.xml");
        try {
            source.createNewFile();
        } catch (IOException e) {
            logger.error("Failed to create file", e);
            throw new IllegalStateException("Failed to create file", e);
        }
        convertToXml.fillXML(source, dao.getAll(tableName));

        File target = new File(directory + "2.xml");
        File xslt;
        try {
            xslt = new File(this.getClass().getResource("xslt.xsl").toURI());
        } catch (URISyntaxException e) {
            logger.error("Failed to create xslt file", e);
            throw new IllegalStateException("Failed to create xslt file", e);
        }
        convertToXml.transformXML(source, target, xslt);

        List<Integer> values = convertToXml.extractNodeAttributes(target, "/entries/entry", columnName);

        printSum(values);
    }
}