package com.cloudera.common.utils

import redis.clients.jedis.{JedisPool, JedisPoolConfig}

case class RedisConfig(redisHost: String, redisPort: Int, redisTimeout: Int, maxTotal: Int, maxIdle: Int, minIdle: Int) extends  Serializable

/**
  * Redis 单机版连接池工具类
  */
object JedisPoolUtils extends Serializable {

  @transient private var pool: JedisPool = null

  def makePool(redisConfig:RedisConfig): Unit ={
    makePool(redisConfig.redisHost, redisConfig.redisPort, redisConfig.redisTimeout, redisConfig.maxTotal, redisConfig.maxIdle, redisConfig.minIdle)
  }

  def makePool(redisHost: String, redisPort: Int, redisTimeout: Int, maxTotal: Int, maxIdle: Int, minIdle: Int): Unit = {
    makePool(redisHost, redisPort, redisTimeout, maxTotal, maxIdle, minIdle, true, false, 10000)
  }

  def makePool(redisHost: String, redisPort: Int, redisTimeout: Int, maxTotal: Int, maxIdle: Int, minIdle: Int, testOnBorrow: Boolean, testOnReturn: Boolean, maxWaitMillis: Long): Unit = {
    if (pool == null) {
      val poolConfig = new JedisPoolConfig()
      poolConfig.setMaxTotal(maxTotal)
      poolConfig.setMaxIdle(maxIdle)
      poolConfig.setMinIdle(minIdle)
      poolConfig.setTestOnBorrow(testOnBorrow)
      poolConfig.setTestOnReturn(testOnReturn)
      poolConfig.setMaxWaitMillis(maxWaitMillis)
      pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout)

      val hook = new Thread {
        override def run = pool.destroy()
      }
      sys.addShutdownHook(hook.run)
    }
  }

  def getPool: JedisPool = {
    assert(pool != null)
    pool
  }
}
