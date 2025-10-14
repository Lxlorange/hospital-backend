package com.itmk.config.batis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
 
@Configuration
@MapperScan("com.itmk.*.*.mapper")
public class BatisPlusConfig {


    /**
     * 乐观锁插件 (OptimisticLockerInnerInterceptor)
     * @return OptimisticLockerInnerInterceptor
     */
    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 防全表更新与删除插件 (BlockAttackInnerInterceptor)
     * @return BlockAttackInnerInterceptor
     */
    @Bean
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    /**
     * SQL性能规范插件 (IllegalSqlInnerInterceptor) - 示例
     * @return IllegalSqlInnerInterceptor
     */
    // @Bean
    // public com.baomidou.mybatisplus.extension.plugins.inner.IllegalSqlInnerInterceptor illegalSqlInnerInterceptor() {
    //     return new com.baomidou.mybatisplus.extension.plugins.inner.IllegalSqlInnerInterceptor();
    // }

    /**
     * 逻辑删除功能所需的SQL注入器
     * (如果您的实体包含 @TableLogic 注解，则需要此Bean)
     * @return ISqlInjector
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new DefaultSqlInjector();
    }

    /**
     * 数据库ID自增 (需要数据库表主键设置为自增)
     * @return KeyGenerator
     */
    // @Bean
    // public com.baomidou.mybatisplus.core.incrementer.IKeyGenerator keyGenerator() {
    //     return new com.baomidou.mybatisplus.core.incrementer.DefaultKeyGenerator();
    // }
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
