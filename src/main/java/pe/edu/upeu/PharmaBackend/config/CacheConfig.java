package pe.edu.upeu.PharmaBackend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import pe.edu.upeu.PharmaBackend.dto.CategoriaResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoResponseDTO;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class CacheConfig {
    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);
    private static final Set<String> CACHE_NAMES = Set.of("categorias", "productos", "productoPorId");

    @Bean
    @ConditionalOnProperty(name = "app.cache.type", havingValue = "redis", matchIfMissing = true)
    CacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                   @Value("${app.cache.ttl:10m}") Duration ttl) {
        var mapper = JsonMapper.shared();
        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl);
        var typeFactory = mapper.getTypeFactory();
        var categoriasSerializer = new JacksonJsonRedisSerializer<List<CategoriaResponseDTO>>(
                mapper, typeFactory.constructCollectionType(List.class, CategoriaResponseDTO.class));
        var productosSerializer = new JacksonJsonRedisSerializer<List<ProductoResponseDTO>>(
                mapper, typeFactory.constructCollectionType(List.class, ProductoResponseDTO.class));
        var productoSerializer = new JacksonJsonRedisSerializer<>(mapper, ProductoResponseDTO.class);
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                "categorias", defaults.serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(categoriasSerializer)),
                "productos", defaults.serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(productosSerializer)),
                "productoPorId", defaults.serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(productoSerializer)));
        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .enableStatistics()
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache.type", havingValue = "simple")
    CacheManager localCacheManager() {
        return new ConcurrentMapCacheManager(CACHE_NAMES.toArray(String[]::new));
    }

    @Bean
    CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Cache read failed; continuing with source data | cache={}", cache.getName());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Cache write failed | cache={}", cache.getName());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Cache eviction failed | cache={}", cache.getName());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Cache clear failed | cache={}", cache.getName());
            }
        };
    }
}
