package com.chinaBank.utils.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service(value = "testService")
public class TestService {
	@Transactional
	public int addBlackLists(String file) throws ExecutionException, InterruptedException {
	    try {
	        BigExcelReader bigExcelReader = new BigExcelReader(file) {

	            @Override
	            public void outputAllRow(List<String[]> lists) {
	                List<List<Object>> lists1 = new LinkedList<>();


	                //执行保存
	                for(String[] row : lists){
	                Map<String, Object> map = new HashMap<String, Object>();
	                //map.put("", row);
	                    List<Object> list2 = new ArrayList<>();
	                    System.out.print("[");
	                    for(String cell : row){
	                        System.out.print(cell+",");
	                        list2.add(cell);
	                    }

	                    lists1.add(list2);
	                    //list2= new ArrayList<Object>();

	                    System.out.println("]");
	                }
	                saveAll(lists1);
	            }
	        };
	        // 执行解析
	        bigExcelReader.parse();
	        //File files = new File(file);

	    } catch (Exception e) {
	       throw new RuntimeException(e);
	    }
	    return 1;

	}

	public void saveAll(List<List<Object>> lists) {
	    if(lists.size() > 0){
	        String uuidotherid = null;
	       Map<String, Object> uuidmap = new HashMap<>();
	        uuidmap.put("uuidotherid",null);
	        for(int i = 2 ;i < lists.size() ;i++){

	            List<Object> list = lists.get(i);

	            if(list.get(0)!= null && !list.get(0).equals("")){
	                Map<String, Object> map = new HashMap<>();
	                String userid = (String) list.get(0);
	                map.put("userid",userid);

	                String addss = (String) list.get(1);
	                map.put("addss",addss/*.substring(0,addss.indexOf(","))*/);
	                String namess = (String) list.get(2);
	                map.put("namess",namess/*.substring(0,namess.indexOf(","))*/);
	                String ages = (String) list.get(3);
	                //ages.substring(0,ages.indexOf(","));
	                Integer age = ages == null  ? null : Integer.valueOf(ages);
	                /*if(ages!=null && !ages.equals("")){
	                    age = Integer.valueOf(ages);

	                }*/
	                map.put("age", age);

//	                uuidotherid = System.getUUID();
//	                map.put("uuidtest",uuidotherid);
	                uuidmap.put("uuidotherid",uuidotherid);
//	                testMapper.saveAll(map);
	                map = null;
	            }else{
	                Map<String, Object> map2 = new HashMap<>();
	                map2.put("otherid", uuidmap.get("uuidotherid"));
	                String othername = (String) list.get(4);
	                map2.put("othername", othername/*.substring(0,othername.indexOf(","))*/);
	                String state = (String) list.get(5);
	                map2.put("state", state/*.substring(0,state.indexOf(","))*/);
//	                testMapper.saveOthers(map2);
	                map2 = null;
	            }

	        }

	    }

	}
}
