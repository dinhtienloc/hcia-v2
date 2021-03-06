package uet.jcia.entities;

import java.io.Serializable;

public class Column implements Serializable {
    
    private static final long serialVersionUID = -4810176750275854793L;
    
    private String name;
	private String type;
	private boolean primaryKey;
	private boolean autoIncrement;
	private boolean notNull;
	private boolean foreignKey; 
	private String length;
	
	private Table table;
	
	private String refXml;
	private String tempId;
	private String tableId;
	private String hbmTag;
	
	public Column(){

	}
	
	@Override
    public Object clone() throws CloneNotSupportedException {
	    return super.clone();
	}
	
	public String getHbmTag() {
        return hbmTag;
    }
	
	public void setHbmTag(String hbmTag) {
        this.hbmTag = hbmTag;
    }
	
	public boolean isForeignKey() {
        return foreignKey;
    }
	
	public void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getRefXml() {
        return refXml;
    }

    public void setRefXml(String refXml) {
        this.refXml = refXml;
    }
    
    public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getTempId() {
        return tempId;
    }
    
    public void setTempId(String tempId) {
        this.tempId = tempId;
    }
    
    public String getTableId() {
        return tableId;
    }
    
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @Override
    public String toString() {
        return "\n    Column ["
                + "name=" + name + ", type=" + type + ", length=" + length + ", primaryKey=" + primaryKey + ", autoIncrement=" + autoIncrement
                + ", foreignKey= " + foreignKey + ", notNull=" + notNull + ", tempId=" + tempId + ", tableId=" + tableId + "]";
    }

}