package com.tbj.aspectj;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.tbj.bean.Account1;
import com.tbj.bean.Account1Log;
import com.tbj.bean.RepayLog;
import com.tbj.enums.HashTypeEnums;
import com.tbj.enums.RepayLogStatusEnums;
import com.tbj.producer.MessageProducer;
import com.tbj.service.Account1LogService;

@Component
@Aspect
public class RepayAspectJ {
	
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private MessageProducer messageProducer;
	
	@Autowired
	private Account1LogService account1LogService;

	/**
	 * execution 第一个 * 表示返回值
	 * 			   第二个参数 表示某个方法的全类名+方法名
	 *           第三个 .. 表示参数类型可以是任意类型 	
	 */
	@Before("execution(* com.tbj.service.impl.Account1ServiceImpl.modify(..))")
	public void before(JoinPoint joinPoint){
		System.out.println("消息生成端");
		Account1 model = (Account1) joinPoint.getArgs()[0];
		
		//将数据插入到本地日志表
		Account1Log model1 = new Account1Log();
		model1.setKey(model.getKey());
		model1.setCompanyId(model.getCompanyId());
		model1.setMoney(model.getBalance());
		account1LogService.insert(model1);
		
		//如果此处出现异常,那么插入到log中的数据会进行回滚，估此处出现异常不会导致数据不一致问题
		//int i = 10 / 0;
		
		RepayLog repayLog = new RepayLog();
		repayLog.setKey(model.getKey());
		repayLog.setCompanyId(model.getCompanyId());
		repayLog.setMoney(model.getBalance());
		repayLog.setNum(0);
		repayLog.setStatus(RepayLogStatusEnums.PRE_SEND.getVal());
		repayLog.setCreateTime(new Date());
		String json = JSON.toJSONString(repayLog);
		Map<String, String> map = new HashMap<String, String>();
		map.put(model.getKey(), json);
		//模拟redis挂掉(测试成功)(一)
		//int i = 10 / 0;
		redisTemplate.opsForHash().putAll(HashTypeEnums.PRE_DATA.getVal(), map);
		
	}
	
	
	/**
	 * 在此方法中如果出现异常，那么主方法照样回滚
	 * @param joinPoint
	 * @param entity
	 */
	@AfterReturning(returning = "entity",value = "execution(* com.tbj.service.impl.Account1ServiceImpl.modify(..))")
	public void after(JoinPoint joinPoint,Object entity){
		//还款处理成功
		//将记录在repayLog中的记录发送到MQ中.
		System.out.println("deal success : " + entity);
		
		Account1 m = (Account1) joinPoint.getArgs()[0];
		
		try {
			//这块需要先将数据存入到 suc 中，不然先发送 MQ， clms会直接出发监听进行处理，如果这个时候，数据还没有存放到 suc 中，那么clms 中会报错误。
			
			//将该条记录从预发送hash中移入到发送成功中
			Map<String, String> map = new HashMap<String, String>();
			RepayLog model = JSON.parseObject(redisTemplate.opsForHash().get(HashTypeEnums.PRE_DATA.getVal(), m.getKey()).toString(), RepayLog.class);
			map.put(m.getKey(), JSON.toJSONString(model));
			//模拟此时redis挂掉(二) 测试成功
			//int i = 10 / 0;
			redisTemplate.opsForHash().putAll(HashTypeEnums.SUC_SEND_DATA.getVal(), map);
			
			//模拟MQ挂掉(三) 测试成功
			//int i = 10 / 0;
			messageProducer.send(m.getKey());
			
			//模拟此处redis挂掉，这个时候 同一条数据存在了两个 hash中。我们可以在 clms中做判断，如果同一个key即存在pre中有存在 suc中，那么即删除 pre 中的数据。
			//模拟redis挂掉(四) 测试成功
			//int i = 10 / 0;  
			//然后删除预发送中的记录
			
			//System.out.println("模拟数据库挂掉,直接关闭mysql数据库");
			
			//Thread.sleep(20000);
			
			//System.out.println("睡眠结束");
			
			//当数据库挂掉之后还是可以继续执行的.
			redisTemplate.opsForHash().delete(HashTypeEnums.PRE_DATA.getVal(), m.getKey());
			
			System.out.println("tangbojian");
		} catch (Exception e) {
			//如果此处出现异常,不做任何处理
			//1：如果是MQ发送不成功，那么到时候会有定时任务做再次发送处理   ： 估这一步出现错误也没有影响。
			//2：如果在将该条记录放入到 成功 Hash 中出现异常，那么在我的处理端，在根据key在成功数据中查找时，找不到对应的记录，那么直接ack回执，删除MQ 队列中的消息，估这一步出现错误也没有影响。
			//3：如果第三部出现异常，那么在我的消费端，我们在处理MQ消息的时候先去预发送数据中查找，如果找到，那么删除此条数据。  故在这一步出现异常也不影响主体
			System.out.println("出现异常");
		}
	}
	
	/**
	 * 当主方法发生异常时.
	 * @param e
	 */
	@AfterThrowing(throwing = "e",value = "execution(* com.tbj.service.impl.Account1ServiceImpl.modify(..))")
	public void throwDeal(Throwable  e){
		//当主方法发生异常，那么删除此条记录.如果此时redis也挂了，那么此处会出现帐不平的情况
		//处理方法：在每次对公司金额进行操作时，记录一条本地历史数据表，然后在跑预处理任务的时候进行对比，如果历史数据表中不存在此数据，那么在删除redis中的数据
		System.out.println("excep");
		
		//模拟此处redis挂掉,那么会产生脏数据，脏数据在 redis 的 pre hash 中，因为此处redis挂掉，估不能删除掉，所以在做定时任务定时将 redis 的 hash pre中的数据
		//重新发送的时候，一定要记住查询此 pre 数据是否存在于 account1_log 表中。如果存在，那么可以进行重新发送，如果不存在，那么删除 redis pre 中的 此条数据
		//模拟此时redis挂掉(五) 测试成功
		//int i = 10 / 0; 
		//redisTemplate.opsForHash().delete(HashTypeEnums.PRE_DATA.getVal(), key);
	}
	
	
}
