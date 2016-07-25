package cuong;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import uet.jcia.entities.Column;
import uet.jcia.entities.Relationship;
import uet.jcia.entities.Table;
import uet.jcia.model.CoreAPI;

public class TestCoreAPI {

    static CoreAPI api = new CoreAPI();
    
    public static void main(String[] args) {
        testZip();
//        testXml();
        
    }
    
    public static void testZip() {
        String resultPath = api.parse("I:\\Workspace\\hcia-v2\\temp\\upload\\vnu.zip");
        System.out.println("[temp data] " + resultPath);
        
        List<Table> tableList = api.getTableList(resultPath);
        
        System.out.println(tableList);
        
        Table modifiedTable = tableList.get(0);
        
        //Relationship [referColumn=CUSTOMER_ID, referTable=CUSTOMER,
        //referClass=cuong.data.sample.Customer, type=many-to-one, tempId=5, tableId=0]
        Relationship modifiedRela = modifiedTable.getListRelationship().get(0);
        
        Table tbl = new Table();
        Relationship rela = new Relationship();
        
        System.out.println("\n\n\n\tModify table");
        System.out.println("RefXml: " + modifiedTable.getRefXml());
        tbl.setRefXml(modifiedTable.getRefXml());
        System.out.println("TempId: " + modifiedTable.getTempId());
        tbl.setTempId(modifiedTable.getTempId());
        
        System.out.println("\n\n\n\tModify relationship");
        System.out.println("TempId: " + modifiedRela.getTempId());
        rela.setTempId(modifiedRela.getTempId());
        System.out.println("Refer table name: " + "PRODUCT");
        rela.setReferTable("PRODUCT");
        System.out.println("Refer column name: " + "PRODUCT_ID");
        rela.setReferColumn("PRODUCT_ID");
        System.out.println("Type: " + modifiedRela.getType());
        rela.setType(modifiedRela.getType());
        
        List<Relationship> relaList = new ArrayList<>();
        relaList.add(rela);
        tbl.setListRelationship(relaList);
        
        List<Table> updatedTableList = new ArrayList<>();
        updatedTableList.add(tbl);
        api.updateData(updatedTableList);
        
        System.out.println(api.download(resultPath));
    }

    public static void testXml() {
        String resultPath = api.parse("I:\\Workspace\\hcia-v2\\temp\\upload\\OrderItem.hbm.xml");
        System.out.println("[temp data] " + resultPath);
        
        List<Table> tableList = api.getTableList(resultPath);
        
        Table modifiedTable = tableList.get(0);
        Column modifiedCol = modifiedTable.getListColumn().get(1);
        Relationship modifiedRel = modifiedTable.getListRelationship().get(0);
        
        System.out.println(tableList);
        
        // update data 
        Table tbl = new Table();
        
        // sửa thông tin table
        System.out.println("\n\n\n\tModify table");
        // cần lấy được refXml của table
        System.out.println("RefXml: " + modifiedTable.getRefXml());
        tbl.setRefXml(modifiedTable.getRefXml());
        // cũng cần lấy đc temp_id của nó
        System.out.println("TempId: " + modifiedTable.getTempId());
        tbl.setTempId(modifiedTable.getTempId());
        
        // sửa tên table thành NEW_ORDERITEM
        System.out.println("New table name: NEW_ORDERITEM");
        tbl.setTableName("NEW_ORDERITEM");
        
        // sửa column
        Column col = new Column();
        System.out.println("\tModify column");
        // cần lấy được temp_id của column
        System.out.println("TempId: " + modifiedCol.getTempId());
        col.setTempId(modifiedCol.getTempId());
        
        // sửa tên
        System.out.println("New name: NEW_NAME");
        col.setName("NEW_NAME");
        
        // sửa kiểu
        System.out.println("New type: VARCHAR");
        col.setType("VARCHAR");
        
        // sửa độ dài
        System.out.println("New length: 15");
        col.setLength("15");
        
        // tạo List<Column> rồi add vào table
        List<Column> colList = new ArrayList<>();
        colList.add(col);
        tbl.setListColumn(colList);
        
        // sửa relationship
        Relationship rel = new Relationship();
        System.out.println("\tModify relationship");
        // cần lấy được temp_id của relationship
        System.out.println("TempId: " + modifiedRel.getTempId());
        rel.setTempId(modifiedRel.getTempId());
        
        // sửa tên bảng tham chiếu
        System.out.println("New name: CUSTOMER");
        rel.setReferTable("CUSTOMER");
        
        // sửa tên cột tham chiếu
        System.out.println("New refer column: CUSTOMER");
        rel.setReferColumn("CUSTOMER_ID");
        
        // tạo List<Column> rồi add vào table
        List<Relationship> relList = new ArrayList<>();
        relList.add(rel);
        tbl.setListRelationship(relList);
        
        // tạo List<Table> để truyền cho CoreAPI.updateData()
        List<Table> tblList = new ArrayList<>();
        tblList.add(tbl);
        
        // gọi hàm updateData()
        api.updateData(tblList);
        
        System.out.println(api.download(resultPath));
    }
}

