package com.yhzt.leasing.config.db;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBContext {
  private static final ThreadLocal<DBTypeEnum> dbContext = new ThreadLocal<>();

  private static final AtomicInteger counter = new AtomicInteger(-1);

  public static void set(DBTypeEnum dbType) {
    dbContext.set(dbType);
  }

  public static DBTypeEnum get() {
    return dbContext.get();
  }

  public static void master() {
    set(DBTypeEnum.MASTER);
    log.info("切换到 Master 库");
  }

  public static void slave() {
    // 读库负载均衡(轮询方式)
    int index = counter.getAndIncrement() % 2;
    // log.info("slave库访问线程数==>{}", counter.get());

    set(DBTypeEnum.SLAVE);
    log.info("线程 : " + index + " 切换到 Slave 库");
    /*
     * if (index == 0) { set(DBTypeEnum.SLAVE1); log.info("切换到slave1库"); } else {
     * set(DBTypeEnum.SLAVE2); log.info("切换到slave2库"); }
     */
  }
}