package com.tbj.test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.tbj.bean.Account1;
import com.tbj.bean.Account1Log;
import com.tbj.bean.RepayLog;
import com.tbj.enums.HashTypeEnums;
import com.tbj.producer.MessageProducer;
import com.tbj.service.Account1LogService;
import com.tbj.service.Account1Service;
import com.tbj.task.Task;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml"})
public class TestRabbitMQ {

	@Autowired
	private MessageProducer messageProducer;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private Account1Service account1Service;
	
	@Autowired
	private Account1LogService account1LogService;
	
	@Autowired
	private Task task;
	
	@Test
	public void test() throws InterruptedException{
		/*String message = "my name is tangbojian";
		messageProducer.send(message);*/
		//redisTemplate.opsForHash().pu
		//设置此次还100元
		BigDecimal moeny = new BigDecimal(100);
		String key = UUID.randomUUID().toString().replaceAll("-", "");
		System.out.println(key);
		String companyId = "1001";
		RepayLog log = new RepayLog();
		log.setMoney(moeny);
		log.setCompanyId(companyId);
		log.setStatus("awaitdeal");
		String json = JSON.toJSONString(log);
		redisTemplate.opsForHash().put("repay", key, json);
		
		//模拟业务逻辑处理时间
		Thread.sleep(5000);
		try {
			messageProducer.send(key);
		} catch (Exception e) {
			//如果发送失败,业务回滚,删除redis 中的数据
			//模拟业务回滚
			Thread.sleep(1000);
			redisTemplate.opsForHash().delete("repay", key);
		}
		
	}
	
	@Test
	public void index(){
		Account1 model = new Account1();
		model.setCompanyId("1001");
		model.setBalance(new BigDecimal(100));
		model.setKey("351ee9d6816e404ba8cf0e8333f46e16");
		account1Service.modify(model);
	}
	
	@Test
	public void testAccount1Log(){
		Account1Log model = new Account1Log();
		model.setCompanyId("1001");
		model.setMoney(new BigDecimal(100));
		model.setKey("351ee9d6816e404ba8c70e8333f46e16");
		try {
			account1LogService.insert(model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int countByKey = account1LogService.countByKey("351ee9d6816e404ba8c70e8333f46e16");
		
		System.out.println(countByKey);
	}
	
	
	@Test
	public void thredTest(){
		int num = Runtime.getRuntime().availableProcessors();
		//num = 1;
		ExecutorService threadPool = Executors.newFixedThreadPool(num);
		
		for(int i = 0 ; i < 5000; i++){
			threadPool.submit(task);
		}
		
		try {
			Thread.sleep(600000 * 6);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		threadPool.shutdown();
	}
	
	
	
	
	
	
	/**
	 * 定时去执行 预发送 hash中的任务,
	 * 适合  情况(二) 情况(三)
	 */
	@Test
	public void imitateTask(){
		HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		//获取所有的key
		Set<Object> keys = opsForHash.keys(HashTypeEnums.PRE_DATA.getVal());
		for(Object obj : keys){
			//先查询该记录是否存在
			int countByKey = account1LogService.countByKey(obj.toString());
			if(countByKey != 1){
				//表示该 redis 的pre 数据有问题。将其删除掉
				redisTemplate.opsForHash().delete(HashTypeEnums.PRE_DATA.getVal(), obj);
				System.out.println("!=1");
				continue;
			}
			//将该条记录从预发送hash中移入到发送成功中
			Map<String, String> map = new HashMap<String, String>();
			RepayLog model = JSON.parseObject(redisTemplate.opsForHash().get(HashTypeEnums.PRE_DATA.getVal(), obj).toString(), RepayLog.class);
			map.put(obj.toString(), JSON.toJSONString(model));
			redisTemplate.opsForHash().putAll(HashTypeEnums.SUC_SEND_DATA.getVal(), map);
			//然后根据key 获取所有的val,然后发送到消息队列
			messageProducer.send(obj);
			//然后删除预发送中的记录
			redisTemplate.opsForHash().delete(HashTypeEnums.PRE_DATA.getVal(), obj);
		}
	}
	
	@Test
	public void dele(){
		HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		//获取所有的key
		Set<Object> keys = opsForHash.keys(HashTypeEnums.SUC_SEND_DATA.getVal());
		
		for(Object obj : keys){
			//先查询该记录是否存在
			redisTemplate.opsForHash().delete(HashTypeEnums.SUC_SEND_DATA.getVal(), obj);
		}
	}
	
	@Test
	public void get(){
		HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		//获取所有的key
		Set<Object> keys = opsForHash.keys(HashTypeEnums.SUC_SEND_DATA.getVal());
		
		String str = "";
		
		for(Object obj : keys){
			if(obj.equals(obj))
			str = (String) obj;
		}
	}
	
	@Test
	public void tran() throws InterruptedException{
		Account1 model = new Account1();
		model.setCompanyId("1001");
		model.setBalance(new BigDecimal(100));
		account1Service.update(model);
		
		System.out.println("start");
		Thread.sleep(20000);
		System.out.println("end");
		Account1Log model1 = new Account1Log();
		model1.setCompanyId("1001");
		model1.setMoney(new BigDecimal(100));
		model1.setKey("351ee9d6816e404ba8c70e8333f46e16");
		account1LogService.insert(model1);
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
		RedisTemplate<String, Object> redisTemplate = applicationContext.getBean("redisTemplate", RedisTemplate.class);
		/*MessageProducer bean = applicationContext.getBean(MessageProducer.class);
		String message = "my name is tangbojian----";
		int a = 10;
		while(a > 0) {
			bean.send(message + a);
			a--;
		}*/
		MessageProducer messageProducer = applicationContext.getBean(MessageProducer.class);
		//设置此次还100元
		BigDecimal moeny = new BigDecimal(100);
		String key = UUID.randomUUID().toString().replaceAll("-", "");
		System.out.println(key);
		String companyId = "1001";
		RepayLog log = new RepayLog();
		log.setMoney(moeny);
		log.setCompanyId(companyId);
		log.setStatus("awaitdeal");
		String json = JSON.toJSONString(log);
		System.out.println(json);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, json);
		redisTemplate.opsForHash().putAll("repay", map);
		//模拟业务逻辑处理时间
		Thread.sleep(2000);
		try {
			messageProducer.send(key);
		} catch (Exception e) {
			//如果发送失败,业务回滚,删除redis 中的数据
			//模拟业务回滚
			Thread.sleep(1000);
			redisTemplate.opsForHash().delete("repay", key);
		}
		
		System.out.println("SUCCESS");
	}
}
