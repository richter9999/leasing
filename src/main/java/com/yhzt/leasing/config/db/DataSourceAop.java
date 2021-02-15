package com.yhzt.leasing.config.db;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAop {
  
  @Pointcut("@annotation(com.yhzt.leasing.annotation.Master) " +
          "|| execution(* com.yhzt.leasing.service..*.insert*(..)) " +
          "|| execution(* com.yhzt.leasing.service..*.add*(..)) " +
          "|| execution(* com.yhzt.leasing.service..*.update*(..)) " +
          "|| execution(* com.yhzt.leasing.service..*.edit*(..)) " +
          "|| execution(* com.yhzt.leasing.service..*.delete*(..)) " +
          "|| execution(* com.yhzt.leasing.service..*.remove*(..))")
  public void writePointcut() {

  }

  @Pointcut("!@annotation(com.yhzt.leasing.annotation.Master) " +
          "&& (execution(* com.yhzt.leasing.service..*.select*(..)) " +
          "|| execution(* com.yhzt.leasing.service..*.get*(..)))")
  public void readPointcut() {

  }

  @Before("writePointcut()")
  public void write() {
    DBContext.master();
  }

  @Before("readPointcut()")
  public void read() {
    DBContext.slave();
  }
}