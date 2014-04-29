package org.safehaus.kiskis.mgmt.impl.sqoop;

import org.safehaus.kiskis.mgmt.api.sqoop.setting.*;

public class CommandFactory {

    public static final String PACKAGE_NAME = "ksks-sqoop";
    private static final String EXEC_PROFILE = ". /etc/profile";

    public static String build(CommandType type, CommonSetting settings) {
        String s = null;
        switch(type) {
            case LIST:
                s = "dpkg -l | grep '^ii' | grep ksks";
                break;
            case INSTALL:
            case PURGE:
                StringBuilder sb = new StringBuilder("apt-get --force-yes --assume-yes ");
                sb.append(type.toString().toLowerCase()).append(" ");
                sb.append(PACKAGE_NAME);
                s = sb.toString();
                break;
            case IMPORT:
                if(settings instanceof ImportSetting)
                    s = importData((ImportSetting)settings);
                break;
            case EXPORT:
                if(settings instanceof ExportSetting)
                    s = exportData((ExportSetting)settings);
                break;
            default:
                throw new AssertionError(type.name());
        }
        return s;
    }

    private static String exportData(ExportSetting settings) {
        StringBuilder sb = new StringBuilder();
        sb.append(EXEC_PROFILE).append(" && ");
        sb.append("sqoop export");
        appendOption(sb, "connect", settings.getConnectionString());
        appendOption(sb, "username", settings.getUsername());
        appendOption(sb, "password", settings.getPassword());
        appendOption(sb, "table", settings.getTableName());
        appendOption(sb, "export-dir", settings.getHdfsPath());
        return sb.toString();
    }

    private static String importData(ImportSetting settings) {
        boolean all = settings.getBooleanParameter(ImportParameter.IMPORT_ALL_TABLES);
        StringBuilder sb = new StringBuilder();
        sb.append(EXEC_PROFILE).append(" && ");
        switch(settings.getType()) {
            case HDFS:
                if(all) sb.append("sqoop-import-all-tables");
                else sb.append("sqoop-import");
                appendOption(sb, "connect", settings.getConnectionString());
                appendOption(sb, "username", settings.getUsername());
                appendOption(sb, "password", settings.getPassword());
                if(!all) appendOption(sb, "table", settings.getTableName());
                break;
            case HBASE:
                sb.append("sqoop import");
                appendOption(sb, "connect", settings.getConnectionString());
                appendOption(sb, "username", settings.getUsername());
                appendOption(sb, "password", settings.getPassword());
                appendOption(sb, "table", settings.getTableName());
                appendOption(sb, "hbase-create-table", null);
                appendOption(sb, "hbase-table", settings.getStringParameter(
                        ImportParameter.DATASOURCE_TABLE_NAME));
                appendOption(sb, "column-family", settings.getStringParameter(
                        ImportParameter.DATASOURCE_COLUMN_FAMILY));
                break;
            case HIVE:
                if(all) sb.append("sqoop-import-all-tables");
                else sb.append("sqoop-import");
                appendOption(sb, "connect", settings.getConnectionString());
                appendOption(sb, "username", settings.getUsername());
                appendOption(sb, "password", settings.getPassword());
                appendOption(sb, "hive-import", null);
                if(!all) {
                    appendOption(sb, "table", settings.getTableName());
                    String db = settings.getStringParameter(ImportParameter.DATASOURCE_DATABASE);
                    String tb = settings.getStringParameter(ImportParameter.DATASOURCE_TABLE_NAME);
                    appendOption(sb, "hive-table", db + "." + tb);
                }
                break;
            default:
                throw new AssertionError(settings.getType().name());
        }
        return sb.toString();
    }

    private static void appendOption(StringBuilder sb, String option, String value) {
        sb.append(" --").append(option);
        if(value != null) sb.append(" ").append(value);
    }
}
